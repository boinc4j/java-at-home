# Java@Home

This is a template application that demonstrates how to use boinc4j and the [BOINC Heroku buildpack]().
It contains a complete implementation of a test for the Goldbach conjecture.

Here's how to use it:

### Step 1: Deploy

Deploy this app by pushing this button:

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

Heroku will ask you for an "App Name" and a "HEROKU_APP_NAME" configuration variable. These values must be
identical as shown below:

![App Name](https://dl.dropboxusercontent.com/u/1935391/boinc4j_app_name.png)

Fill out the other variables, and then click "Deploy for Free". Once Heroku is finished compiling the app and building
the BOINC server, you'll be able to access it by clicking the "View" button. You'll see a standard BOINC project with
Server Status and even an Ops interface.

### Step 2: Clone

Now you'll need to clone the project to your local machine so you can work with it. First, install the Heroku toolbelt,
and then login with your account by running:

```
$ heroku login
```

Now clone your app by running this command, but replace "my-boinc-app" with the name you used in Step 1 for "App Name":

```
$ heroku git:clone -a my-boinc-app
```

Now you'll have a complete Maven project. You can open it in Eclipse or IntelliJ and start hacking on it. But first,
let's test the server.

### Step 3: Connect a client

Install the [BOINC Client](http://boinc.berkeley.edu/download.php) software for your machine. Then open it, and
add a new project. When asked for a project URL, use the landing page you viewed in Step 1
(for example: http://my-boinc-app.herokuapp.com/boinc/).

### Step 4: Create some work

This sample app tests numbers for the Goldbach conjection (i.e. all even numbers are the sum of two primes).
We can give the BOINC server a chunk of numbers that we what it to crunch by using the `create_work` script
included in this repo. It expects the following arguments:

```
create_work <START_SUM> <CHUNK_SIZE> <NUM_OF_JOBS>
```

Thus, you can ask your clients to check numbers starting with 10, each checking 8 numbers, and create 2 jobs
(thus checking numbers from 10 to 36) by running this command:

```
$ heroku run sh create_work 10 8 2
```

Once the work is created, update the project in your client (or just wait until it refreshes, and you'll it start
working). You can check the server's status page to see the progress of these jobs.

### Step 5: Check the results

TODO: create a status page

## Usage

* Fork this repo
* Add your custom logic to the `MainTask` class
* Add your assimilation logic to the `Assimilator` class
* Click the Heroku button from your repo.

