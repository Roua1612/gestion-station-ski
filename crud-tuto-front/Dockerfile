# Stage 1, "build-stage", based on Node.js, to build and compile the frontend
FROM node:12.20-alpine as build
WORKDIR /app
COPY package*.json /app/
RUN npm install --legacy-peer-deps
COPY ./ /app/
ARG configuration=production
RUN npm run ng build -- --output-path=dist --configuration=$ENVIROMENT --verbose


# Stage 2, based on Nginx, to have only the compiled app, ready for production with Nginx
FROM nginx:1.17.1-alpine
COPY --from=build /app/dist /usr/share/nginx/html
# Expose port 80
EXPOSE 80
