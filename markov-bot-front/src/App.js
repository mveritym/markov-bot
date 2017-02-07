import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props){
    super(props);
      this.state = {result: ""};
  }

  componentDidMount() {
    fetch("https://fjgriz49mh.execute-api.us-west-2.amazonaws.com/prod/clj-hello?users=mveritym")
      .then((response) => response.json())
      .then((responseJson) => {this.setState({result: responseJson})});
  }

  render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to React</h2>
        </div>
        <p className="App-intro">
          {this.state.result}
        </p>
      </div>
    );
  }
}

export default App;
