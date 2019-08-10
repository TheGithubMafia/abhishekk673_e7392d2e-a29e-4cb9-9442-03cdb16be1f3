const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Authenticate to Algolia Database.
// TODO: Make sure you configure the `algolia.app_id` and `algolia.api_key` Google Cloud environment variables.
const algoliasearch = require('algoliasearch');
const algolia = algoliasearch("IZDGA4GCBK", "0d030bc6e78332bbbc83f85017c87eec");

const dotenv = require('dotenv');
const firebase = require('firebase');

// load values from the .env file in this directory into process.env
dotenv.config();

// configure firebase
firebase.initializeApp({
  databaseURL: "https://bookselling-af219.firebaseio.com/",
});
const database = firebase.database();

// configure algolia

const index = algolia.initIndex("Books");


// Get all contacts from Firebase
exports.indexUpdate=database.ref('/Books').once('value', Books => {
  // Build an array of all records to push to Algolia
  const records = [];
  Books.forEach(contact => {
    // get the key and data from the snapshot
    const childKey = Book.key;
    const childData = Book.val();
    // We set the Algolia objectID as the Firebase .key
    childData.objectID = childKey;
    // Add object for indexing
    records.push(childData);
  });

  // Add or update new objects
  index
    .saveObjects(records)
    .then(() => {
      console.log('Contacts imported into Algolia');
    })
    .catch(error => {
      console.error('Error when importing contact into Algolia', error);
      process.exit(1);
    });
});

