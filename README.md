Assembla ALM integration with Jenkins
=====================================

Overview
--------
This plugin integrates Assembla to Jenkins, allowing the detection and 
association of Tickets / Cards to Builds and generated artifacts.

Jenkins push back to Assembla feedback of build execution, allowing
all the stakeholders associated to be notified in real-time, even when
they do not have access or visibility to the Jenkins CI instance.

Setup
-----
Into the Jenkins global configuration screen configure the Assembla site 
url and the authentication credentials: those are used to login to Assembla
on behalf of Jenins.

You can optionally set a custom pattern to detect Assembla tickets in commit messages.
