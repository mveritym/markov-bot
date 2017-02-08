import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import AWS from 'aws-sdk';

class App extends Component {

  constructor(props){
    super(props);
    this.state = {
      result: "",
      users: "",
      searches: "",
      isLoading: false
    };
  }

  componentDidMount() {
    AWS.config.region = 'us-west-2'; // Region
    AWS.config.credentials = new AWS.CognitoIdentityCredentials({
        IdentityPoolId: 'us-west-2:9b63f1e6-e3b8-4c2f-9592-532633cf2992',
    });
  }

  handleUsersChange = (event) => {
    this.setState({users: event.target.value})
  }

  handleSearchChange = (event) => {
    this.setState({searches: event.target.value})
  }

  combineWithCommas = (inputStr) => inputStr.split('\n').join(',');

  generateTweets(event) {
    event.preventDefault();
    this.setState({
      isLoading: true,
      result: ""
    });

    const lambda = new AWS.Lambda({region: 'us-west-2', apiVersion: '2015-03-31'});
    const pullParams = {
      FunctionName : 'clj-hello',
      InvocationType : 'RequestResponse',
      LogType : 'None',
      Payload: `{
        "users": "${this.combineWithCommas(this.state.users)}",
        "searches": "${this.combineWithCommas(this.state.searches)}"
      }`
    };

    lambda.invoke(pullParams, (error, data) => {
      this.setState({isLoading: false});
      if (error) {
        prompt(error);
      } else {
        this.setState({result: JSON.parse(data.Payload)});
      }
    })
  }

  render() {
    return (
      <div className="App">
        <div className="App-header">
          <h2>Generate Some Tweets!</h2>
        </div>
        <h4>Enter usernames or search terms separated by new lines:</h4>
        <form name="tweets-search-form">
          <div className="input-box">
            <label>Users: </label>
            <textarea rows="10" value={this.state.users} onChange={this.handleUsersChange}></textarea>
          </div>
          <div className="input-box">
            <label>Searches: </label>
            <textarea rows="10" value={this.state.searches} onChange={this.handleSearchChange}></textarea>
          </div>
          <button type="submit" onClick={this.generateTweets.bind(this)}>Generate tweets</button>
        </form>
        <div className="App-intro">
          {Object.values(this.state.result).map((result, i) => <p key={i}>{result}</p>)}
          {this.state.isLoading ? <img src={logo} className="App-logo" alt="logo" /> : null}
        </div>
      </div>
    );
  }
}

export default App;
