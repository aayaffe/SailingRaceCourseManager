#![alt tag](https://raw.githubusercontent.com/aayaffe/SailingRaceCourseManager/master/app/src/main/res/drawable/managergold.png?token=ACtzsiSB2DCgb66JeZURu0c9fvwhkH6xks5XJR2EwA%3D%3D) AVI - Sailing Race Course Manager
Develop: [![Build Status](https://travis-ci.org/aayaffe/SailingRaceCourseManager.svg?branch=develop)](https://travis-ci.org/aayaffe/SailingRaceCourseManager)
Master: [![Build Status](https://travis-ci.org/aayaffe/SailingRaceCourseManager.svg?branch=master)](https://travis-ci.org/aayaffe/SailingRaceCourseManager)

[![Coverage Status](https://coveralls.io/repos/github/aayaffe/SailingRaceCourseManager/badge.svg?branch=master)](https://coveralls.io/github/aayaffe/SailingRaceCourseManager?branch=master)

 [![SonarQube Coverage](https://img.shields.io/sonar/http/www.sonarqube.com/AVI:master/coverage.svg?label=SonarQubeCoverage)]()
 [![SonarQube Tech Debt](https://img.shields.io/sonar/http/www.sonarqube.com/AVI:master/tech_debt.svg)]()

![alt tag](https://raw.githubusercontent.com/aayaffe/SailingRaceCourseManager/master/Banner.png?token=ACtzsozA--o3IXB_F9GhcaP8f3wUw3Rjks5XJR75wA%3D%3D)


Installation:

1. Download code

2. Using the keystore.properties.template file create a keystore.properties file in your base directory with the values and passwords of your signing keys.
(According to this [tutorial](https://developer.android.com/studio/publish/app-signing.html#secure-shared-keystore))

3. Add the google-services.json (Downloaded from Firebase console) into the app directory or release\staging\debug directory (According to the build flavor required).

4. Add google-maps-api.xml into the [BUILD_VARIANT]\res\values directory (https://developers.google.com/maps/documentation/android-api/start#step_4_get_a_google_maps_api_key)

5. Add a firebase_url.xml into the [BUILD_VARIANT]\res\values directory

6. Enable email authentication in your Firebase project

7. Change the firebase database security rules to:

{
  "rules": {
    ".read": true,
    ".write": true
  }
}

* [BUILD_VARIANT] is release or staging or debug

This is an app to help Sailing Races officials with laying a race course.
