FROM node:18

# Create app directory
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install --omit=dev

# Copy source code
COPY . .

# Default backend port (doesn't matter for container networking)
EXPOSE 3000

# Start backend
CMD ["npm", "start"]
