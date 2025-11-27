<p align="center"><img src="logo.png" alt="Agama-Profile-Edit logo" style="height: 500px; width:500px;"/></p>


<!-- These are statistics for this repository-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache License][license-shield]][license-url]

# About Agama-Profile-Edit Project

This repo is the home of the Gluu Agama-profile-edit project. This Agama project allows users to update their profile after email-based authentication. For email-based authentication, we have a community project called Agama-SMTP. Here, we use the Agama-SMTP project as an example of reusing a community project and applying template overriding.

## Where To Deploy

The project can be deployed to any IAM server that runs an implementation of 
the [Agama Framework](https://docs.jans.io/head/agama/introduction/) like 
[Janssen Server](https://jans.io) and [Gluu Flex](https://gluu.org/flex/).

## How To Deploy

Different IAM servers may provide different methods and 
user interfaces from where an Agama project can be deployed on that server. 
The steps below show how the Agama-profile-edit project can be deployed on the 
[Janssen Server](https://jans.io). 

Deployment of an Agama project involves three steps.

- [Downloading the `.gama` package from the project repository](#download-the-project)
- [Adding the `.gama` package to the IAM server](#add-the-project-to-the-server)
- [Configure the project](#configure-the-project)

#### Pre-Requisites

Here we re-use our existing **Agama-SMTP** project, So It's mendatory to deploye **Agama-SMTP** project from community project. To send email messages, ensure you have the Jans Auth Server with 
[SMTP service](https://docs.jans.io/head/admin/config-guide/smtp-configuration/)
configured

### Download the Project

> [!TIP]
> Skip this step if you use the Janssen Server TUI tool to 
> configure this project. The TUI tool enables the download and adding of this 
> project directly from the tool, as part of the `community projects` listing. 


The project is bundled as 
[.gama package](https://docs.jans.io/head/agama/gama-format/). 
Visit the `Assets` section of the 
[Releases](https://github.com/GluuFederation/agama-profile-edit/releases) to download the `.gama` package.

### Add The Project To The Server

The Janssen Server provides multiple ways an Agama project can be 
deployed and configured. Either use the command-line tool, REST API, or a To send email messages, ensure you have the Jans Auth Server set up. It includes an SMTP service for sending emails, but you need to configure it before use.
TUI (text-based UI). Refer to 
[Agama project configuration page](https://docs.jans.io/head/admin/config-guide/auth-server-config/agama-project-configuration/) 
in the Janssen Server documentation for more details.

### Configure The Project

The Agama project accepts configuration parameters in the JSON format. 
Every Agama project comes with a basic sample configuration file for reference.


Below is a typical configuration of the Agama-SMTP project. As shown, it contains
configuration parameters for the [flows contained in it](#flows-in-the-project):

```
{
  "org.gluu.agama.profile.update.main": {
  }
}
```

### Test The Flow

Use any relying party implementation (like [jans-tarp](https://github.com/JanssenProject/jans/tree/main/demos/jans-tarp)) 
to send an authentication request that triggers the flow.

From the incoming authentication request, the Janssen Server reads the `ACR` 
parameter value to identify which authentication method should be used. 
To invoke the `org.gluu.agama.profile.update.main` flow contained in the Agama-Profile-Edit project, 
specify the ACR value as `agama_<qualified-name-of-the-top-level-flow>`, 
i.e `agama_org.gluu.agama.profile.update.main`.


## Flows In The Project

List of the flows: 

- [org.gluu.agama.profile.update.main](#orggluuagamaprofileupdatemain)

### org.gluu.agama.profile.update.main

The main flow of this project is [org.gluu.agama.profile.update.main](./code/org.gluu.agama.profile.update.main.flow) .
In step one, the person enters their email address, to which the IDP sends an OTP code.
After OTP verification, if the email is recognized, the user is shown a profile update form. Upon submission, the system updates the user’s profile (if validation succeeds) and then completes the flow with an acknowledgment message
If the email address is new, the IDP displays a registration form.


## Demo


<!-- This are stats url reference for this repository -->
[contributors-shield]: https://img.shields.io/github/contributors/GluuFederation/agama-profile-edit.svg?style=for-the-badge
[contributors-url]: https://github.com/GluuFederation/agama-profile-edit/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/GluuFederation/agama-profile-edit.svg?style=for-the-badge
[forks-url]: https://github.com/GluuFederation/agama-profile-edit/network/members
[stars-shield]: https://img.shields.io/github/stars/GluuFederation/agama-profile-edit?style=for-the-badge
[stars-url]: https://github.com/GluuFederation/agama-profile-edit/stargazers
[issues-shield]: https://img.shields.io/github/issues/GluuFederation/agama-profile-edit.svg?style=for-the-badge
[issues-url]: https://github.com/GluuFederation/agama-profile-edit/issues
[license-shield]: https://img.shields.io/github/license/GluuFederation/agama-profile-edit.svg?style=for-the-badge
[license-url]: https://github.com/GluuFederation/agama-profile-edit/blob/main/LICENSE
