# Vanity Numbers

This project contains source code and supporting files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders:

- `HelloWorldFunction/src/main/java/helloworld/SuggestVanityNumber`: Code for the Suggesting VanityFunction Lambda
- `HelloWorldFunction/src/main/java/helloworld/QueryLatestItemHandler`: Code for displaying the latest 5 entries from DynamoDB
- `HelloWorldFunction/src/main/java/helloworld/ContactFlowPublisher`: Code for creating contact flow using lambda and setting lambda ARN, which will be invoked by the contact flow
- `ContactFlow.json`: Script for contact flow
- `template.yml`: The application uses several AWS resources, including Lambda functions, DynamoDB, contact flow, and an API Gateway API. These resources are defined in the `template.yaml` file in this project. You can update the template to add AWS resources through the same deployment process that updates your application code.

## Deploy Application

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment and API.

To use the SAM CLI, you need the following tools:

- SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- Java 21 - [Install Java 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
- Maven - [Install Maven](https://maven.apache.org/install.html)

To build and deploy your application for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```

The first command will build the source of your application. The second command will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modifies IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided. If permission isn't provided through this prompt, to deploy this example you must explicitly pass `--capabilities CAPABILITY_IAM` to the `sam deploy` command.
* **Save arguments to samconfig.toml**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.

You can find your API Gateway Endpoint URL in the output values displayed after deployment.

## Use the SAM CLI to build and test locally

Build your application with the `sam build` command.

```bash
vanitynumbers$ sam build
```

The SAM CLI installs dependencies defined in `HelloWorldFunction/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
sam delete --stack-name vanitynumbers
```

## Resources

See the [AWS SAM developer guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) for an introduction to SAM specification, the SAM CLI, and serverless application concepts.

Next, you can use AWS Serverless Application Repository to deploy ready to use Apps that go beyond hello world samples and learn how authors developed their applications: [AWS Serverless Application Repository main page](https://aws.amazon.com/serverless/serverlessrepo/)

## Challenges
1. For creating a contact flow through lambda, I am getting an InvalidRequestException continuously.
  What I tried:
    1. Passed a simple contact flow creation to check if there is anything wrong with the script - still not working.
    2. Created a Python lambda, and still, I was getting an InvalidRequestException - still not working.
    3. Added so many loggers to check if values of passed params - values were all okay.
  I am suspecting this is something related to my connect instance. I will explore this further.

2. I have worked with Go language + AWS, but I know JAVA but never used Java for AWS Lambda functions. So I wanted to explore JAVA SDK for AWS. There were quite a lot of challenges during the setup phase, but above all, I managed to set it up from scratch.

3. Due to time crunch, I could not test this code. I just deployed it, and it is getting deployed properly.

## Improvements
1. IAM roles needs to be refined
2. Custome Contact Flow creation needs to worked upon
3. Test cases can be added
4. Lambda code needs to be refactored, currently everything is in single file
5. Error Handling needs to be improved


## Design 
<img width="851" alt="image" src="https://github.com/mbudhe/aws-lambda-vanity-numbers/assets/6449344/052c442d-7bc7-4774-b73a-4aaf0830aa57">

## Contact Flow
<img width="1508" alt="image" src="https://github.com/mbudhe/aws-lambda-vanity-numbers/assets/6449344/937be216-fe25-4ed4-a64d-24fe6858d852">





