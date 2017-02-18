import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import AWS from 'aws-sdk';
import { getBots } from './aws/db';

class App extends Component {

  constructor(props){
    super(props);
    this.state = {
      bots: [],
      result: "",
      users: "",
      botName: "",
      selectedBot: "",
      isLoading: false
    };
  }



  async componentDidMount() {
    AWS.config.region = 'us-west-2'; // Region
    AWS.config.credentials = new AWS.CognitoIdentityCredentials({
        IdentityPoolId: 'us-west-2:9b63f1e6-e3b8-4c2f-9592-532633cf2992',
    });
    this.lambda = new AWS.Lambda({region: 'us-west-2', apiVersion: '2015-03-31'});

    const bots = await getBots();
    this.setState({
      bots,
      botName: bots[0]['bot-name'],
      users: bots[0]['user-names'].join("\n")
    });
  }

  handleUsersChange = (event) => {
    this.setState({users: event.target.value});
  }

  handleBotNameChange = (event) => {
    this.setState({botName: event.target.value});
  }

  handleSelectedBotChange = (event) => {
    const selectedBotName = event.target.value;
    console.log("New users:", this.state.bots.find(bot => bot['bot-name'] === selectedBotName)['user-names']);
    this.setState({
      selectedBot: selectedBotName,
      botName: selectedBotName,
      users: this.state.bots.find(bot => bot['bot-name'] === selectedBotName)['user-names'].join("\n")
    });
  }

  combineWithCommas = (inputStr) => inputStr.split('\n').join(',');

  makeBot(event) {
    event.preventDefault();
    this.setState({isLoading: true});

    const botName = this.state.botName;
      const pullParams = {
      FunctionName : 'MakeBot',
      InvocationType : 'RequestResponse',
      LogType : 'None',
      Payload: `{
        "users": "${this.combineWithCommas(this.state.users)}",
        "bot-name": "${botName}"
      }`
    };

    this.lambda.invoke(pullParams, (error, data) => {
      this.setState({isLoading: false});
      if (error) {
        prompt(error);
      } else {
        const newBots = this.state.bots.slice();
        if (this.state.bots.indexOf(botName) === -1) {
          newBots.push({
            'bot-name': botName,
            'users': this.state.users.split("\n")
          });
        }
        this.setState({
          bots: [...newBots],
          selectedBot: botName
        })
      }
    });
  }

  runBot(event) {
    event.preventDefault();
    this.setState({isLoading: true, result: ""});

    const selectedBot = this.state.selectedBot;
    const pullParams = {
      FunctionName: 'RunBot',
      InvocationType: 'RequestResponse',
      LogType: 'None',
      Payload: `{
        "bot-name": "${selectedBot}"
      }`
    };

    this.lambda.invoke(pullParams, (error, data) => {
      this.setState({isLoading: false});
      if (error) {
        prompt(error);
      } else {
        this.setState({result: JSON.parse(data.Payload)})
      }
    })
  }

  render() {
    return (
      <div className="App">
        <div className="App-header">
          <h2>{`Generate Some Tweets!`}</h2>
        </div>
        <div className="App-body">
          {this.state.isLoading ?
            <div className="darkClass">
              <div className="logo-container">
                <img src={logo} className="App-logo" alt="logo" />
              </div>
            </div> : null}
          <div className="column">
            <h3>{`Make A Bot`}</h3>
            <form>
              <label>Bot Name:</label>
              <input type="text" value={this.state.botName} onChange={this.handleBotNameChange}></input>
              <label>Twitter usernames (1 per line):</label>
              <textarea rows="10" value={this.state.users} onChange={this.handleUsersChange}></textarea>
              <button type="submit" onClick={this.makeBot.bind(this)}>Make Bot</button>
            </form>
          </div>
          <div className="column">
            <h3>Bots:</h3>
            {this.state.bots.length !== 0 ?
              <form>
                <label>&nbsp;</label>
                <select value={this.state.selectedBot} onChange={this.handleSelectedBotChange}>
                  {this.state.bots.map((bot, i) => {
                    const botName = bot['bot-name'];
                    return <option key={i} value={botName}>{botName}</option>;
                  })}
                </select>
                <button type="submit" onClick={this.runBot.bind(this)}>Run Bot</button>
              </form> : <p>No bots yet!</p>}

          </div>
          <div className="column">
            <h3>Tweets:</h3>
            {this.state.result ?
              <div>
                <label>&nbsp;</label>
                <ul>
                  {Object.values(this.state.result).map((result, i) => <li key={i}>{result}</li>)}
                </ul>
              </div>
              : <p>No tweets yet!</p>}
          </div>
        </div>
      </div>
    );
  }
}

export default App;
