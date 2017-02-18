import AWS from 'aws-sdk';

AWS.config.region = 'us-west-2'; // Region
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
    IdentityPoolId: 'us-west-2:9b63f1e6-e3b8-4c2f-9592-532633cf2992',
});
const lambda = new AWS.Lambda({region: 'us-west-2', apiVersion: '2015-03-31'});

const docClient = new AWS.DynamoDB.DocumentClient();

export const getBots = () => {
  const params = {
    TableName: "Bots"
  };

  return new Promise((resolve, reject) => {
    docClient.scan(params, (err, data) => {
      if (err) reject("Fetch bots failed");
      resolve(data.Items);
    })
  });
}

const combineWithCommas = (inputStr) => inputStr.split('\n').join(',');

export const makeBot = (bots, botName, users) => {
  const pullParams = {
    FunctionName : 'MakeBot',
    InvocationType : 'RequestResponse',
    LogType : 'None',
    Payload: `{
      "users": "${combineWithCommas(users)}",
      "bot-name": "${botName}"
    }`
  };

  return new Promise((resolve, reject) => {
    lambda.invoke(pullParams, (error, data) => {
      if (error) reject("Make bot lambda failed");
      else {
        const newBots = bots.slice();
        const botNames = bots.map(bot => bot['bot-name']);
        if (botNames.indexOf(botName) === -1) {
          newBots.push({
            'bot-name': botName,
            'users': users.split("\n")
          });
        }
        resolve({
          bots: [...newBots],
          selectedBot: botName
        });
      }
    });
  });
}
