# Auto Deploy in Amazon EC2 on Git Commit using GitHub CI/CD Action and AWS CodeDeploy.

***

## Prerequisite
***
* [GitHub](https://github.com) account.
* [AWS](https://console.aws.amazon.com/console/home) Account.

## Table Of Contents
***
* [Create IAM Role for EC2 and CodeDeploy](#create-iam-role)
* [Create EC2 Instance](#create-ec2)
* [Launch EC2 Instance](#launch-ec2)
* [Install CodeDeploy Agent on EC2 Instance](#install-codedeploy-agent)
* [CodeDeploy Service Configuration](#codedeploy-service-config)
* [GitHub Project](#github-project)
* [GitHub Action](#github-action)


### Note
******
> Select a particular region of AWS Services which CodeDeploy Agent and GitHub will use.


### Create IAM Role For EC2 and CodeDeploy <a name="create-iam-role"/>
***

![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/26bulqgskw5bpu64r0kl.png)

***

Create a role for **EC2 Instance** - 
  1. Select **AWS Service** as _trusted entity_ and **EC2** as _usecase_, click on _Next:Permissions_.
  2. On the Permissions page, select **AmazonEC2RoleforAWSCodeDeploy** Policy and Click on _Next:Tags_
  3. Ignore the tags and click _Next:Review_.
  4. Provide the role name as **EC2_Role** on the review page.
  ![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/46kuzgo4qxpc639girdl.png)
  5. Open the EC2_Role and go to _Trust Relationships_, then _Edit Trust Relationship_ and paste below policy -
  
  ```json
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Service": "ec2.amazonaws.com"
        },
        "Action": "sts:AssumeRole"
      }
    ]
  }
  ```

  ![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/irtiqqgvv9uig7zxlist.png)


***

Now we will create a role for **CodeDeploy**.<a name="codedeploy_role"/>
  1. Select *AWS Service* as _trusted entity_ and *EC2* as _usecase_, click on _Next:Permissions_.
  2. On the Permissions page, select the below policy and Click on _Next:Tags_.
`AmazonEC2FullAccess, AWSCodeDeployFullAccess, AdministratorAccess, AWSCodeDeployRole`
  3. Tags can be ignored, click on _Next:Review_.
  4. Provide the role name as **CodeDeploy_Role** on the review page.
  ![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/y6l3k4yjo76yvbwu2ut1.png)
  5. Once CodeDeploy Role created, Open the CodeDeploy_Role and go to _Trust Relationships_ then _Edit Trust Relationship_ and use below policy -
    ```json
    {
      "Version": "2012-10-17",
      "Statement": [
        {
          "Effect": "Allow",
          "Principal": {
            "Service": "codedeploy.amazonaws.com"
          },
          "Action": "sts:AssumeRole"
        }
      ]
    }
    ```

***
### Create EC2 Instance<a name="create-ec2"/>
To create an EC2 instance, Go to EC2 Dashboard on AWS Management Console and click on **Launch Instance**.

On the AIM page, You can select any _Volume Type_ based on your requirement. This article will choose _Free Tier_ **Amazon Linux 2 AMI (HVM), SSD Volume Type** and **64-bit (x86)** Volume and click on _select_.

Select **t2.micro** in _Choose Instance Typ_ page and proceed to _Configure Instance_ page.

 To establish the connection between EC2 instance and codeDeploy, Select **EC2_Role**, which we created before.

On the _Tag page_, add a tag called **development**. The tag will require creating a _codeDeploy_ service.

In _Configure Security Group_ page _Add Rule_ called **All traffic**, select _source_ called **anywhere**.
  > This rule will enable you to connect the Instance from anywhere.
  **NOTE** - This is not advisable in the Production environment.

Select the _review_ page, then _Launch_ the Instance. Wait for a few minutes to start the EC2 Instance.
  > If you want to access the Instance (ssh) from your local system, create a new _Key Pair_ and download the key.


![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/tsbfckiuolr4y1oj5ilt.png)
![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/c66iy10uzgmv6dygz5cl.png)


***

### Launch EC2 Instance <a name="launch-ec2"/>

Once Instance is up and running, Right-click on _instance id_ and click on _connect_.

On the next page, Take a note of the **Public IP Address** and _connect_ using the default **User name**.


![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/wd9853dwfnc7hldoy7n9.png)

### Install CodeDeploy Agent on EC2 Instance <a name="install-codedeploy-agent"/>
> TO Deploy the git repo by using CodeDeploy Service, **codeDeploy-agent** must install in the EC2 instance.

Use the below commands to install codedeploy-agent.

```shell
sudo yum update
```

```shell
sudo yum install -y ruby
```

```shell
sudo yum install wget
```

```shell
wget https://bucket-name.s3.region-identifier.amazonaws.com/latest/install
```
> bucket-name is the Amazon S3 bucket containing the CodeDeploy Resource Kit files for your region. region-identifier is the identifier for your region.
> [list of bucket names and region identifiers](https://docs.aws.amazon.com/codedeploy/latest/userguide/resource-kit.html#resource-kit-bucket-names)
>> For example - `wget https://aws-codedeploy-ap-south-1.s3.ap-south-1.amazonaws.com/latest/install`

```shell
chmod +x ./install
```

```shell
sudo ./install auto
```

```shell
sudo service codedeploy-agent start 
```


***


### CodeDeploy Service Configuration <a name="codedeploy-service-config"/>
***
AWS [CodeDeploy Service](https://docs.amazonaws.cn/en_us/codedeploy/latest/userguide/welcome.html) will automate the GitHub application deployment to EC2.

Create an Application name called **Git_Application** with _compute platform_ **EC2/On-premises**.
> [GitHub Action](#github-action-code) will use the application name.
![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/hciskx6rrwq0r4m3whw6.png)

***
Once Application Created, Create a _Deployment Group_ and name **development_gropup**. Get the *Role ARN* from [CodeDeploy_Role](#codedeploy_role), which we created before and put in the service role.
> [GitHub Action](#github-action-code) will use the deployment Group name.

Choose **In-place** _Deployment type_. Select _Amazon Ec2 Instances_ environment configuration and Tag key **development** to create AWS EC2 instance.

Select a schedule manager to install the CodeDeploy agent. Set _OneAtATime_ deployment setting and Create Deployment Group without a load balancer.

![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/gqw1plmd8tpjscukpr7d.png)
![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/41sxab2cuhni6412y7f5.png)
![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/3xivhirkebhcrl9ne7kb.png)

***
Once Deployment Group created, test the deployment by creating a Deployment with any name.

Select _Revision Type_ **My application is stored in GitHub**, and select **Connect to GitHub** by providing the [GitHub token](https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token).

Once connected to GitHub, Provide the [repository name](#github-repository) and last _Commit ID_. Select _Overwrite the content_ and Create Deployment.


![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/3s5f3cv3auw4magmt33c.png)
![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/qya9yce6v5zxb09c21um.png)
  
Wait for a few minutes ⏳ .

If Deployment status is unsuccessful, verify the deployment logs from ec2 instance `/var/log/aws/codedeploy-agent/codedeploy-agent.log`.

Recreate the deployment and fix this first. Once it's successful, you can access the application from a web browser or postman.

```curl
curl --location --request GET 'http://{{[ec2_public_ip]}}:8080/student'
```
Get `ec2_public_ip` from [EC2 Instance](#launch-ec2)

***
### GitHub Project<a name="github-project"/>

[Fork](https://docs.github.com/en/github/getting-started-with-github/quickstart/fork-a-repo) the [spring-boot demo project](https://github.com/ankushbehera/spring-boot-mongo) repository.

This project is a spring-boot project which uses MongoDB. 
For project deployment, we will use [docker-compose](https://docs.docker.com/compose), which includes MongoDB.

> The [`appspec.yml`](https://docs.aws.amazon.com/codedeploy/latest/userguide/reference-appspec-file.html) file used by codeDeploy to manage the deployment. 

> The `setup.sh` will install docker and docker-compose. 

> The `run.sh` is used for `docker-compose up`.

```yaml
version: 0.0
os: linux
files:
  - source: .
    destination: /home/ec2-user/spring-boot-mongo/
hooks:
  AfterInstall:
   - location: setup.sh
     timeout: 300
     runas: root
  ApplicationStart:
   - location: run.sh
     timeout: 300
     runas: root
```

***
### GitHub Action <a name="github-action"/>

First, create an [IAM user](https://docs.amazonaws.cn/en_us/IAM/latest/UserGuide/id_users_create.html#id_users_create_console) with full `AWSCodeDeployFullAccess` policy and generate an [access key and secret access](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html#Using_CreateAccessKey) for the user to configure GitHub Action.

Before configuring Action, set the environment in the GitHub repository.

![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/zkmvr6y8pi9aqpdjlj6b.png)

GitHub repository changes will trigger [GitHub Action](https://docs.github.com/en/actions), which has two CI/CD job - 
> The continuous-integration job will compile the code and run the JUnit Test cases.
> The continuous-deployment job will call AWS CodeDeploy Service -
>> application - **Git_Application**
>> deployment-group - **development_gropup**
 
Paste below YAML in action configuration and commit.
<a name="github-action-code"/> 
```yaml
name: CI/CD Pipeline
on:
  push:
    branches: [ main ]

jobs:
  continuous-integration:
    runs-on: ubuntu-latest
    steps:
      # Step 1
      - uses: actions/checkout@v2
      # Step 2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'
      # Step 3
      - name: Build Application and Run unit Test
        run: mvn -B test --file student-service/pom.xml
        
  continuous-deployment:
    runs-on: ubuntu-latest
    needs: [continuous-integration]
    if: github.ref == 'refs/heads/main'
    steps:
     # Step 1
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ secrets.AWS_REGION }}
     # Step 2
      - name: Create CodeDeploy Deployment
        id: deploy
        run: |
          aws deploy create-deployment \
            --application-name Git_Application \
            --deployment-group-name development_gropup \
            --deployment-config-name CodeDeployDefault.OneAtATime \
            --github-location repository=${{ github.repository }},commitId=${{ github.sha }}
```

<a name="github-repository"/>

![image](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/azrf0bp3qkm44qef91x3.png)
 
Now make a change to your repository. Your changes should automatically deploy to your EC2 server.

Access the application from a web browser or postman.

```curl
curl --location --request GET 'http://{{[ec2_public_ip]}}:8080/student'
```
Get `ec2_public_ip` from [EC2 Instance](#launch-ec2)
 

