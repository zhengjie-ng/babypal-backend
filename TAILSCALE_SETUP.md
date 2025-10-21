# Tailscale Setup for AWS Elastic Beanstalk

This document explains how to set up Tailscale on your AWS Elastic Beanstalk deployment to access your database securely.

## Prerequisites

1. A Tailscale account and network set up
2. The database server (100.83.60.58:5434) already connected to Tailscale
3. AWS Elastic Beanstalk environment configured

## Setup Steps

### 1. Generate a Tailscale Auth Key

1. Go to [Tailscale Admin Console](https://login.tailscale.com/admin/settings/keys)
2. Click "Generate auth key"
3. Configure the key:
   - **Reusable**: Yes (recommended for auto-scaling)
   - **Ephemeral**: No (unless you want nodes to be automatically removed)
   - **Pre-approved**: Yes (to avoid manual approval)
   - **Tags**: Add appropriate tags if needed
4. Copy the generated auth key (starts with `tskey-auth-`)

### 2. Set the Auth Key in AWS Elastic Beanstalk

You have two options:

#### Option A: Via AWS Console
1. Go to your Elastic Beanstalk environment
2. Navigate to Configuration → Software → Environment properties
3. Add/Update the following environment variable:
   - **Name**: `TAILSCALE_AUTH_KEY`
   - **Value**: Your Tailscale auth key (e.g., `tskey-auth-xxxxxx`)

#### Option B: Via AWS CLI
```bash
aws elasticbeanstalk update-environment \
  --environment-name babypal-backend-docker-env \
  --option-settings \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=TAILSCALE_AUTH_KEY,Value=YOUR_AUTH_KEY_HERE \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=DATABASE_URL,Value=jdbc:postgresql://100.83.60.58:5434/babypal \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=DATABASE_USERNAME,Value=postgres \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=DATABASE_PASSWORD,Value=password
```

### 3. Update the Configuration File (Already Done)

The `.ebextensions/tailscale.config` file has been created with the following configuration:
- Installs Tailscale on instance startup
- Authenticates using the `TAILSCALE_AUTH_KEY` environment variable
- Enables route acceptance to access your database network

### 4. Deploy Your Application

Deploy your application to AWS Elastic Beanstalk:

```bash
# Build and push Docker image to ECR
docker build -t babypal-backend .
docker tag babypal-backend:latest 209759484743.dkr.ecr.ap-southeast-1.amazonaws.com/babypal-backend:latest
aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin 209759484743.dkr.ecr.ap-southeast-1.amazonaws.com
docker push 209759484743.dkr.ecr.ap-southeast-1.amazonaws.com/babypal-backend:latest

# Deploy to Elastic Beanstalk
eb deploy babypal-backend-docker-env
```

### 5. Verify Tailscale Connection

After deployment, SSH into your EC2 instance and verify:

```bash
# Check Tailscale status
sudo tailscale status

# Test database connectivity
nc -zv 100.83.60.58 5434
```

## Configuration Files Modified

1. **`.ebextensions/tailscale.config`** - New file that installs and configures Tailscale
2. **`.env`** - Updated to document the Tailscale database connection

## Database Connection Details

- **Host**: 100.83.60.58 (via Tailscale network)
- **Port**: 5434
- **Database**: babypal
- **Username**: postgres
- **Password**: password

## Troubleshooting

### Tailscale not connecting
1. Check if the auth key is correctly set in environment variables
2. Verify the auth key hasn't expired
3. Check EC2 instance logs: `/var/log/eb-engine.log`

### Database connection fails
1. Verify Tailscale is running: `sudo tailscale status`
2. Check if routes are accepted: Look for "accept-routes" in Tailscale status
3. Test connectivity: `telnet 100.83.60.58 5434`
4. Verify database server allows connections from the EC2 instance's Tailscale IP

### Check Elastic Beanstalk logs
```bash
eb logs
```

## Security Notes

- Never commit your Tailscale auth key to version control
- Use reusable auth keys for auto-scaling environments
- Consider using ephemeral keys for development environments
- Rotate auth keys periodically
- The `.env` file contains sensitive credentials and should never be committed to git

## Additional Resources

- [Tailscale Documentation](https://tailscale.com/kb/)
- [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elasticbeanstalk/)
