import AWS from 'aws-sdk';

AWS.config.region = 'us-west-2'; // Region
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
    IdentityPoolId: 'us-west-2:9b63f1e6-e3b8-4c2f-9592-532633cf2992',
});
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
