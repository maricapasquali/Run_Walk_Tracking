# Use the official PHP 7.4 with Apache base image
FROM php:7.4-apache AS base_server_image

# Update all packages and clean up to reduce vulnerabilities
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

## Install the mysqli PHP extension
RUN docker-php-ext-install mysqli

## Enable the mysqli PHP extension
RUN docker-php-ext-enable mysqli

# Remove the default Virtual Host of Apache
RUN a2dissite 000-default.conf

# Enable the rewrite module if it is not already active
RUN a2enmod rewrite

# Copy custom Apache configuration file.
# This file should define the Virtual Host for site.
COPY ./apache/server.conf /etc/apache2/sites-available/my-site.conf

# Enable custom site
RUN a2ensite my-site.conf

# Install dependencies
RUN apt-get update && apt-get install -y \
    zip unzip git curl && \
    curl -sS https://getcomposer.org/installer | php && \
    mv composer.phar /usr/local/bin/composer

WORKDIR /var/www/html

# Copy application code into the container
COPY . .


### PRODUCTION AND DEVELOPMENT STAGES
# Use the base server image as a starting point for production and development stages

FROM base_server_image AS prod

# Install project dependencies
RUN composer install --optimize-autoloader


FROM base_server_image AS dev

## Install project dependencies and start apache server
ENTRYPOINT composer update && apache2-foreground
