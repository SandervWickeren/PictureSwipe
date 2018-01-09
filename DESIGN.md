# Design
![board1](doc/MockupsV3.png)

Each screen is it's own fragment, all inflated in the mainactivity. The progress is saved in the local sqlite database that contains
all names of the rated pictures. Favorites are saved online to Firebase, and available when logged in on a different device with the
same account. When there is no internet connection the pictures are saved to a specific table "Queued_Favorites" that 'll be uploaded
once the user is back online. The database structure is as follows:

The first table for the sqlite database. Name and date are saved for future reference. (Maybe want to implement that photo's are 
showing up again after a year or something like that).

**Progress**
- _Filename_
- _Date_

The second table for the sqlite database, it's used to keep track of the files it has to process to firebase when there is no internet
connection.

**Favorites**
- _Filename_

Third table is the bin.

**Bin**
- _Filename_

The first table for the Firebase, it's used to save the favorite (important) pictures from the users into the cloud.

**UID**
- _Pictures_

#### Functions and necessary classes
- DeleteAll Pictures
- ListAdapters
- Connection to sqlite database
- Connection to firebase 

_StartFragment_
- Handles buttons
- Loads where you previous where from savedinstance.

_SwipeFragment_
- Functions that loads the pictures
- Function that sends the filename to the right table, even to firebase if connected to internet.
- Function that handles swipe events.

_FavoritesFragment_
- Function that loads pictures from database.
- Function that handles removal from favorites.
- ListAdapater
- Custom listview
- Function that checks if user is online

_BinFragment_
- Function that deletes all pictures
- Listadapter
- Custom listview

_Popupfragment_
- Checks if user is sure doing a certain action.

_PictureFragment_
- Function that show picture fullscreen

_LoginFragment_
- Function that handles login process
- Function that handles button clicks

_RegisterFragment_
- Function that handels register process
- Function that handles button clicks

#### API's and Frameworks:
- Picasso
- Firebase




