#!/bin/bash

# Script to scale the application horizontally

# Check if the number of instances is provided
if [ $# -ne 1 ]; then
    echo "Usage: $0 <number_of_instances>"
    exit 1
fi

# Get the number of instances
NUM_INSTANCES=$1

# Check if the number is valid
if ! [[ "$NUM_INSTANCES" =~ ^[0-9]+$ ]] || [ "$NUM_INSTANCES" -lt 1 ]; then
    echo "Error: Number of instances must be a positive integer"
    exit 1
fi

echo "Scaling to $NUM_INSTANCES instances..."

# Create a temporary docker-compose file
cat > docker-compose.scale.yml << EOF
version: '3.8'

services:
  app:
    deploy:
      replicas: $NUM_INSTANCES
EOF

# Apply the scaling
docker-compose -f docker-compose.yml -f docker-compose.scale.yml up -d

echo "Application scaled to $NUM_INSTANCES instances"

# Update Nginx configuration
echo "Updating Nginx configuration..."

# Create upstream configuration
UPSTREAM_CONF="upstream vehicles-app {\n"
for (( i=1; i<=$NUM_INSTANCES; i++ ))
do
    UPSTREAM_CONF+="    server app_$i:9094;\n"
done
UPSTREAM_CONF+="}\n"

# Update nginx.conf
sed -i "s/upstream vehicles-app {.*}/$(echo -e $UPSTREAM_CONF)/" nginx/nginx.conf

# Reload Nginx
docker-compose exec nginx nginx -s reload

echo "Nginx configuration updated and reloaded"
echo "Application successfully scaled to $NUM_INSTANCES instances"