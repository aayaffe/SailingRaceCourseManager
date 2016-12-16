/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';
// Shortcuts to DOM Elements.
var messageForm = document.getElementById('message-form');
var latInput = document.getElementById('new-lat');
var lonInput = document.getElementById('new-lon');
var boatNameInput = document.getElementById('new-boat-name');
var signInButton = document.getElementById('sign-in-button');
var signOutButton = document.getElementById('sign-out-button');
var splashPage = document.getElementById('page-splash');
var addPost = document.getElementById('add-post');
var addButton = document.getElementById('add');
var eventsSection = document.getElementById('events-list');
var boatsSection = document.getElementById('boats-list');
var buoysSection = document.getElementById('buoys-list');
var mapSection = document.getElementById('boats-map');
var recentMenuButton = document.getElementById('menu-recent');
var myPostsMenuButton = document.getElementById('menu-my-posts');
var myTopPostsMenuButton = document.getElementById('menu-my-top-posts');
var boatsMapMenuButton = document.getElementById('menu-boats-map');

var listeningFirebaseRefs = [];
function generateUUID(){
    var d = new Date().getTime();
    if(window.performance && typeof window.performance.now === "function"){
        d += performance.now(); //use high-precision timer if available
    }
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
    return uuid;
}
/**
 * Saves a new post to the Firebase DB.
 */
// [START write_fan_out]
function writeNewBoat(username, lat, lon) {
  // A post entry.
  var postData = {   
    aviLocation: {
		cog: 0,
		depth: 0,
		lastUpdtae: 1481571920087,
		lat: lat,
		lon: lon,
		sog: 0
	},
	color: -16776961,
	id: 0,
	lastUpdate : 1481571920087,
	name: username,
	type: "RACE_MANAGER",
	uuidString: generateUUID()		
  };

  // Get a key for a new Post.
  //var newPostKey = firebase.database().ref().child('posts').push().key;

  // Write the new post's data simultaneously in the posts list and the user's post list.
  var updates = {};
  updates['/posts/' + username] = postData;

  return firebase.database().ref().update(updates);
}
// [END write_fan_out]

/**
 * Creates a post element.
 */
function createPostElement(postId, boatName, lat, lon, author, authorId, authorPic) {
  var uid = firebase.auth().currentUser.uid;
  var html =
      '<div class="post post-' + postId + ' mdl-cell mdl-cell--12-col ' +
                  'mdl-cell--6-col-tablet mdl-cell--4-col-desktop mdl-grid mdl-grid--no-spacing">' +
        '<div class="mdl-card mdl-shadow--2dp">' +
          '<div class="mdl-card__title mdl-color--light-blue-600 mdl-color-text--white">' +
            '<h4 class="mdl-card__title-text"></h4>' +
          '</div>' +
          '<div class="header">' +
            '<div>' +
              '<div class="avatar"></div>' +
              '<div class="username mdl-color-text--black"></div>' +
            '</div>' +
          '</div>' +
          '<span class="star">' +
            '<div class="not-starred material-icons">star_border</div>' +
            '<div class="starred material-icons">star</div>' +
            '<div class="star-count">0</div>' +
          '</span>' +
          '<div class="lat"></div>' + '<div class="lon"></div>' +
          '<div class="comments-container"></div>' +
          '<form class="add-comment" action="#">' +
            '<div class="mdl-textfield mdl-js-textfield">' +
              '<input class="mdl-textfield__input new-comment" type="text">' +
              '<label class="mdl-textfield__label">Comment...</label>' +
            '</div>' +
          '</form>' +
        '</div>' +
      '</div>';

  // Create the DOM element from the HTML.
  var div = document.createElement('div');
  div.innerHTML = html;
  var postElement = div.firstChild;
  if (componentHandler) {
    componentHandler.upgradeElements(postElement.getElementsByClassName('mdl-textfield')[0]);
  }

  var addCommentForm = postElement.getElementsByClassName('add-comment')[0];
  var commentInput = postElement.getElementsByClassName('new-comment')[0];
  var star = postElement.getElementsByClassName('starred')[0];
  var unStar = postElement.getElementsByClassName('not-starred')[0];

  // Set values.
  postElement.getElementsByClassName('lat')[0].innerText = "Lat: " + lat;
  postElement.getElementsByClassName('lon')[0].innerText = "Lon: " + lon;
  postElement.getElementsByClassName('mdl-card__title-text')[0].innerText = boatName;
  postElement.getElementsByClassName('username')[0].innerText = author || 'Anonymous';
  postElement.getElementsByClassName('avatar')[0].style.backgroundImage = 'url("' +
      (authorPic || './silhouette.jpg') + '")';

  // Listen for comments.
  // [START child_event_listener_recycler]
  var commentsRef = firebase.database().ref('post-comments/' + postId);
  commentsRef.on('child_added', function(data) {
    addBoatElement(postElement, data.key, data.val().text, data.val().author);
  });

  commentsRef.on('child_changed', function(data) {
    setBoatValues(postElement, data.key, data.val().text, data.val().author);
  });

  commentsRef.on('child_removed', function(data) {
    deleteBoat(postElement, data.key);
  });
  // [END child_event_listener_recycler]

   // Keep track of all Firebase reference on which we are listening.
  listeningFirebaseRefs.push(commentsRef);


  // Create new comment.
  addCommentForm.onsubmit = function(e) {
    e.preventDefault();
    createNewComment(postId, firebase.auth().currentUser.displayName, uid, commentInput.value);
    commentInput.value = '';
    commentInput.parentElement.MaterialTextfield.boundUpdateClassesHandler();
  };

  // Bind starring action.
  var onStarClicked = function() {
    var globalPostRef = firebase.database().ref('/posts/' + postId);
    var userPostRef = firebase.database().ref('/user-posts/' + authorId + '/' + postId);
    toggleStar(globalPostRef, uid);
    toggleStar(userPostRef, uid);
  };
  unStar.onclick = onStarClicked;
  star.onclick = onStarClicked;

  return postElement;
}

/**
 * Creates a event element.
 */
function createEventElement(eventId, eventName, managerUuid, boats) {
    var uid = firebase.auth().currentUser.uid;
    var html =
        '<div class="post post-' + eventId + ' mdl-cell mdl-cell--12-col ' +
        'mdl-cell--6-col-tablet mdl-cell--4-col-desktop mdl-grid mdl-grid--no-spacing">' +
        '<div class="mdl-card mdl-shadow--2dp">' +
        '<div class="mdl-card__title mdl-color--light-blue-600 mdl-color-text--white">' +
        '<h4 class="mdl-card__title-text"></h4>' +
        '</div>' +
        '<div class="header">' +
        '<div>' +
        '<div class="avatar"></div>' +
        '<div class="username mdl-color-text--black"></div>' +
        '</div>' +
        '</div>' +
        '<div class="comments-container"></div>' +
        '<form class="add-comment" action="#">' +
        '<div class="mdl-textfield mdl-js-textfield">' +
        '<input class="mdl-textfield__input new-comment" type="text">' +
        '<label class="mdl-textfield__label">Comment...</label>' +
        '</div>' +
        '</form>' +
        '</div>' +
        '</div>';

    // Create the DOM element from the HTML.
    var div = document.createElement('div');
    div.innerHTML = html;
    var eventElement = div.firstChild;
    if (componentHandler) {
        componentHandler.upgradeElements(eventElement.getElementsByClassName('mdl-textfield')[0]);
    }

    var addCommentForm = eventElement.getElementsByClassName('add-comment')[0];
    var commentInput = eventElement.getElementsByClassName('new-comment')[0];

    // Set values.
    eventElement.getElementsByClassName('mdl-card__title-text')[0].innerText = eventName;
    eventElement.getElementsByClassName('username')[0].innerText = managerUuid || 'Anonymous';
    eventElement.getElementsByClassName('avatar')[0].style.backgroundImage = 'url("' +
        (boats || './silhouette.jpg') + '")';

    // Listen for comments.
    // [START child_event_listener_recycler]
    var commentsRef = firebase.database().ref('events/' + eventName);
    commentsRef.on('child_added', function(data) {
        addBoatElement(eventElement, data.key, data.val().text, data.val().author);
    });
    commentsRef.on('child_changed', function(data) {
        setBoatValues(eventElement, data.key, data.val().text, data.val().author);
    });
    commentsRef.on('child_removed', function(data) {
        deleteBoat(eventElement, data.key);
    });
    // [END child_event_listener_recycler]


    // Keep track of all Firebase reference on which we are listening.
    listeningFirebaseRefs.push(commentsRef);

    // Create new comment.
    addCommentForm.onsubmit = function(e) {
        e.preventDefault();
        createNewComment(eventId, firebase.auth().currentUser.displayName, uid, commentInput.value);
        commentInput.value = '';
        commentInput.parentElement.MaterialTextfield.boundUpdateClassesHandler();
    };
    return eventElement;
}
/**
 * Creates a event element.
 */
function createBoatElement(boatId, boatName, location) {
    var uid = firebase.auth().currentUser.uid;
    var html =
        '<div class="post post-' + boatId + ' mdl-cell mdl-cell--12-col ' +
        'mdl-cell--6-col-tablet mdl-cell--4-col-desktop mdl-grid mdl-grid--no-spacing">' +
        '<div class="mdl-card mdl-shadow--2dp">' +
        '<div class="mdl-card__title mdl-color--light-blue-600 mdl-color-text--white">' +
        '<h4 class="mdl-card__title-text"></h4>' +
        '</div>' +
        '<div class="header">' +
        '<div>' +
        '<div class="avatar"></div>' +
        '<div class="username mdl-color-text--black"></div>' +
        '</div>' +
        '</div>' +
        '<div class="comments-container"></div>' +
        '<form class="add-comment" action="#">' +
        '<div class="mdl-textfield mdl-js-textfield">' +
        '<input class="mdl-textfield__input new-comment" type="text">' +
        '<label class="mdl-textfield__label">Comment...</label>' +
        '</div>' +
        '</form>' +
        '</div>' +
        '</div>';

    // Create the DOM element from the HTML.
    var div = document.createElement('div');
    div.innerHTML = html;
    var boatElement = div.firstChild;
    if (componentHandler) {
        componentHandler.upgradeElements(boatElement.getElementsByClassName('mdl-textfield')[0]);
    }

    var addCommentForm = boatElement.getElementsByClassName('add-comment')[0];
    var commentInput = boatElement.getElementsByClassName('new-comment')[0];

    // Set values.
    boatElement.getElementsByClassName('mdl-card__title-text')[0].innerText = boatName;
    boatElement.getElementsByClassName('username')[0].innerText = location.lat + ", " + location.lon || 'Anonymous';
    boatElement.getElementsByClassName('avatar')[0].style.backgroundImage = 'url("' +
        ('./silhouette.jpg') + '")';

    // Listen for comments.
    // [START child_event_listener_recycler]
    var commentsRef = firebase.database().ref('events/' + boatName);
    commentsRef.on('child_added', function(data) {
        addBoatElement(boatElement, data.key, data.val().text, data.val().author);
    });
    commentsRef.on('child_changed', function(data) {
        setBoatValues(boatElement, data.key, data.val().text, data.val().author);
    });
    commentsRef.on('child_removed', function(data) {
        deleteBoat(boatElement, data.key);
    });
    // [END child_event_listener_recycler]


    // Keep track of all Firebase reference on which we are listening.
    listeningFirebaseRefs.push(commentsRef);

    // Create new comment.
    addCommentForm.onsubmit = function(e) {
        e.preventDefault();
        createNewComment(boatId, firebase.auth().currentUser.displayName, uid, commentInput.value);
        commentInput.value = '';
        commentInput.parentElement.MaterialTextfield.boundUpdateClassesHandler();
    };
    return boatElement;
}

/**
 * Creates a boat element and adds it to the given eventElement.
 */
function addBoatElement(eventElement, id, name, author) {
    var comment = document.createElement('div');
    comment.classList.add('comment-' + id);
    comment.innerHTML = '<span class="username"></span><span class="name"></span>';
    comment.getElementsByClassName('name')[0].innerText = name;
    comment.getElementsByClassName('username')[0].innerText = author || 'Anonymous';

    var commentsContainer = eventElement.getElementsByClassName('comments-container')[0];
    commentsContainer.appendChild(comment);
}

/**
 * Sets the boat's values in the given eventElement.
 */
function setBoatValues(postElement, id, name, author) {
    var comment = postElement.getElementsByClassName('comment-' + id)[0];
    comment.getElementsByClassName('name')[0].innerText = name;
    comment.getElementsByClassName('fp-username')[0].innerText = author;
}

/**
 * Deletes the boat of the given ID in the given eventElement.
 */
function deleteBoat(postElement, id) {
    var comment = postElement.getElementsByClassName('comment-' + id)[0];
    comment.parentElement.removeChild(comment);
}

/**
 * Starts listening for new posts and populates posts lists.
 */
function startDatabaseQueries() {
  // [START my_top_posts_query]
  var myUserId = firebase.auth().currentUser.uid;
  var topUserPostsRef = firebase.database().ref('user-posts/' + myUserId).orderByChild('starCount');
  // [END my_top_posts_query]
  // [START recent_posts_query]
  var events = firebase.database().ref('Events');
  // [END recent_posts_query]
  var boats = events.child('עיה/boats');

  var fetchEvents = function(eventsRef, sectionElement) {
        eventsRef.on('child_added', function(data) {
            var author = data.val().managerUuid;
            var containerElement = sectionElement.getElementsByClassName('posts-container')[0];
            containerElement.insertBefore(
                createEventElement(data.val().uuid,data.val().name,author,data.val().boats),
                containerElement.firstChild);
        });
        eventsRef.on('child_changed', function(data) {
            var containerElement = sectionElement.getElementsByClassName('posts-container')[0];
            var eventElement = containerElement.getElementsByClassName('post-' + data.key)[0];
            eventElement.getElementsByClassName('mdl-card__title-text')[0].innerText = data.val().name;
            eventElement.getElementsByClassName('username')[0].innerText = data.val().managerUuid;
        });
        eventsRef.on('child_removed', function(data) {
            var containerElement = sectionElement.getElementsByClassName('posts-container')[0];
            var post = containerElement.getElementsByClassName('post-' + data.key)[0];
            post.parentElement.removeChild(post);
        });
    };
  var fetchBoats = function(boatsRef, sectionElement) {
        boatsRef.on('child_added', function(data) {
            var author = data.val().uuidString;
            var location = data.val().aviLocation;
            var containerElement = sectionElement.getElementsByClassName('posts-container')[0];
            containerElement.insertBefore(
                createEventElement(data.val().uuid,data.val().name,author,data.val().boats),
                containerElement.firstChild);
        });
        boatsRef.on('child_changed', function(data) {
            var containerElement = sectionElement.getElementsByClassName('posts-container')[0];
            var eventElement = containerElement.getElementsByClassName('post-' + data.key)[0];
            eventElement.getElementsByClassName('mdl-card__title-text')[0].innerText = data.val().name;
            eventElement.getElementsByClassName('username')[0].innerText = data.val().managerUuid;
        });
        boatsRef.on('child_removed', function(data) {
            var containerElement = sectionElement.getElementsByClassName('posts-container')[0];
            var post = containerElement.getElementsByClassName('post-' + data.key)[0];
            post.parentElement.removeChild(post);
        });
    };

  // Fetching and displaying all posts of each sections.
  //fetchPosts(topUserPostsRef, buoysSection);
  fetchEvents(events, eventsSection);
  fetchBoats(boats, boatsSection);
  //fetchPosts(topUserPostsRef, mapSection);

  // Keep track of all Firebase refs we are listening to.
  listeningFirebaseRefs.push(topUserPostsRef);
  listeningFirebaseRefs.push(events);
  listeningFirebaseRefs.push(boats);
}

/**
 * Writes the user's data to the database.
 */
// [START basic_write]
function writeUserData(userId, name, email, imageUrl) {
  firebase.database().ref('users/' + userId).set({
    username: name,
    email: email,
    profile_picture : imageUrl
  });
}
// [END basic_write]

/**
 * Cleanups the UI and removes all Firebase listeners.
 */
function cleanupUi() {
  // Remove all previously displayed posts.
  buoysSection.getElementsByClassName('posts-container')[0].innerHTML = '';
  eventsSection.getElementsByClassName('posts-container')[0].innerHTML = '';
  boatsSection.getElementsByClassName('posts-container')[0].innerHTML = '';


    // Stop all currently listening Firebase listeners.
  listeningFirebaseRefs.forEach(function(ref) {
    ref.off();
  });
  listeningFirebaseRefs = [];
}

/**
 * The ID of the currently signed-in User. We keep track of this to detect Auth state change events that are just
 * programmatic token refresh but not a User status change.
 */
var currentUID;

/**
 * Triggers every time there is a change in the Firebase auth state (i.e. user signed-in or user signed out).
 */
function onAuthStateChanged(user) {
  // We ignore token refresh events.
  if (user && currentUID === user.uid) {
    return;
  }

  cleanupUi();
  if (user) {
    currentUID = user.uid;
    splashPage.style.display = 'none';
    writeUserData(user.uid, user.displayName, user.email, user.photoURL);
    startDatabaseQueries();
  } else {
    // Set currentUID to null.
    currentUID = null;
    // Display the splash page where you can sign-in.
    splashPage.style.display = '';
  }
}

/**
 * Creates a new post for the current user.
 */
function newPostForCurrentUser(boatName, lat, lon) {
  // [START single_value_read]
  var userId = firebase.auth().currentUser.uid;
  return firebase.database().ref('/users/' + userId).once('value').then(function(snapshot) {
    var username = snapshot.val().username;
    // [START_EXCLUDE]
    return writeNewBoat(boatName, lat, lon);
    // [END_EXCLUDE]
  });
  // [END single_value_read]
}

/**
 * Displays the given section element and changes styling of the given button.
 */
function showSection(sectionElement, buttonElement) {
  eventsSection.style.display = 'none';
  boatsSection.style.display = 'none';
  buoysSection.style.display = 'none';
  mapSection.style.display=  'none';
  addPost.style.display = 'none';
  recentMenuButton.classList.remove('is-active');
  myPostsMenuButton.classList.remove('is-active');
  myTopPostsMenuButton.classList.remove('is-active');
  boatsMapMenuButton.classList.remove('is-active');

  if (sectionElement) {
    sectionElement.style.display = 'block';
  }
  if (buttonElement) {
    buttonElement.classList.add('is-active');
  }
}

/**
 * Creates a map object with a click listener and a heatmap.
 */
function initMap() {
    var map = new google.maps.Map(document.getElementById('map'), {
        center: new google.maps.LatLng(-34.397, 150.644),
        zoom: 3
    });

    // Create the DIV to hold the control and call the makeInfoBox() constructor
    // passing in this DIV.
    var infoBoxDiv = document.createElement('div');
    makeInfoBox(infoBoxDiv, map);
    map.controls[google.maps.ControlPosition.TOP_CENTER].push(infoBoxDiv);

    // Listen for clicks and add the location of the click to firebase.
    map.addListener('click', function(e) {
        data.lat = e.latLng.lat();
        data.lng = e.latLng.lng();
        addToFirebase(data);
    });
}
function makeInfoBox(controlDiv, map) {
    // Set CSS for the control border.
    var controlUI = document.createElement('div');
    controlUI.style.boxShadow = 'rgba(0, 0, 0, 0.298039) 0px 1px 4px -1px';
    controlUI.style.backgroundColor = '#fff';
    controlUI.style.border = '2px solid #fff';
    controlUI.style.borderRadius = '2px';
    controlUI.style.marginBottom = '22px';
    controlUI.style.marginTop = '10px';
    controlUI.style.textAlign = 'center';
    controlDiv.appendChild(controlUI);

    // Set CSS for the control interior.
    var controlText = document.createElement('div');
    controlText.style.color = 'rgb(25,25,25)';
    controlText.style.fontFamily = 'Roboto,Arial,sans-serif';
    controlText.style.fontSize = '100%';
    controlText.style.padding = '6px';
    controlText.textContent = 'The map shows all clicks made in the last 10 minutes.';
    controlUI.appendChild(controlText);
}

/**
 * Used to get location data based on lat and long
 * based on example https://developers.google.com/maps/documentation/javascript/geocoding
 * @param geocoder
 * @param map
 * @param infowindow
 */

function geocodeLatLng(geocoder, map, infowindow) {
    var input = document.getElementById('latlng').value;
    var latlngStr = input.split(',', 2);
    var latlng = {lat: parseFloat(latlngStr[0]), lng: parseFloat(latlngStr[1])};
    geocoder.geocode({'location': latlng}, function(results, status) {
        if (status === 'OK') {
            if (results[1]) {
                map.setZoom(11);
                var marker = new google.maps.Marker({
                    position: latlng,
                    map: map
                });
                infowindow.setContent(results[1].formatted_address);
                infowindow.open(map, marker);
            } else {
                window.alert('No results found');
            }
        } else {
            window.alert('Geocoder failed due to: ' + status);
        }
    });
}

// Bindings on load.
window.addEventListener('load', function() {
  // Bind Sign in button.
  signInButton.addEventListener('click', function() {
    var provider = new firebase.auth.GoogleAuthProvider();
    firebase.auth().signInWithPopup(provider);
  });

  // Bind Sign out button.
  signOutButton.addEventListener('click', function() {
    firebase.auth().signOut();
  });

  // Listen for auth state changes
  firebase.auth().onAuthStateChanged(onAuthStateChanged);

  // Saves message on form submit.
  messageForm.onsubmit = function(e) {
    e.preventDefault();
    var lat = latInput.value;
	var lon = lonInput.value;
    var boatName = boatNameInput.value;
    if (lat && lon && boatName) {
      newPostForCurrentUser(boatName, lat, lon).then(function() {
        myPostsMenuButton.click();
      });
      latInput.value = '';
	  lonInput.value = '';
      boatNameInput.value = '';
    }
  };

  // Bind menu buttons.
  recentMenuButton.onclick = function() {
    showSection(eventsSection, recentMenuButton);
  };
  myPostsMenuButton.onclick = function() {
    showSection(boatsSection, myPostsMenuButton);
  };
  myTopPostsMenuButton.onclick = function() {
    showSection(buoysSection, myTopPostsMenuButton);
  };
    boatsMapMenuButton.onclick = function() {
        showSection(mapSection, boatsMapMenuButton);
    };
  addButton.onclick = function() {
    showSection(addPost);
    latInput.value = '';
	lonInput.value = '';
    boatNameInput.value = '';
  };
  recentMenuButton.onclick();
}, false);
