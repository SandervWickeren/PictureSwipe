# Report

The app Pickit focusses on the people that have many pictures, and struggle to find a convenient way to manage them.
It allows users to select one of their albums and select if they wanna keep, delete or favorite the pictures, using
a intuitive swipe action. The option to sync favorites to the cloud, makes sure that the user 'll never lose their
precious pictures. They can boot up a new device and sync their favorites without any hassle.

PICTURE

## Technical Design


### High level overview

The application is build up from the idea of actvities and fragments. The MainActivity is de heart of the application
and contains most of the inflated fragments. The navigation at the bottom and the icons at the actionbar guide you 
trough the application. There are three screens presented and the user always start with the album selection fragment.
When selecting the big + button, a intent 'll popup and let the user select an image from the album. After the selection
the backend processes the parent folder and retrieves all the images from this folder. The selection fragment is transitioned
into the fragment containing the SwipeStack (more below). At this point the user can start selecting his or her favorites 
pictures or remove the ones he / she doesn't like. The following actions can be done afterwards:
- The 'deleted' pictures end up in the bin (Sqlite table), which can be reviewed by the user. They can be removed from the bin (not the device) by
long clicking on the image. When the user presses on the Bin found in the ActionBar a popup 'll launch and asks the user if he / she
really wants to remove all the pictures from the device.
- The 'accepted' pictures end in a 'pictures' Sqlite table. In this table are all pictures stored that have been reviewd before.
When the user has reviewed all pictures from a given album, he / she gets the possibility to review the album.
- The 'favorites' pictures end up in an eponymous table and can be viewed just like the bin. When the user is logged in he / she
can upload the favorites to Firebase and download them on another device.

In both favorites and bin the picture can be shown bigger by shortly pressing it.

Classes:
- BinFragment: contains the view of the bin.
- FavoritesFragment: contains the view of the favorites.
- ClearBinDialogFragment: producees a dialogfragment and when agreed removes the bin-pictures from the device.
- FullscreenImageFragment: used to show images bigger.
- GrandPermission: handles the correct permissions.
- LoadImages: Used to load the pictures and convert URI to a path.
- PictureGridAdapter: Used to adapt the pictures into a grid.
- PictureGridHandler: Contains all the functions around longclick and click
- SwipeFragment: contains the view of the swipeable cards.
- SwipeSetupFragment: used to let the user select an album.


#### Syncing with the cloud

An import feature is syncing with the cloud. This process is handled in the FirebaseHelper class. In a few steps
it does the following spread out over several functions:
- Check if file in Firebase Storage:
  - If not:
    - Upload file to Firebase Storage
    - Make a reference with the Uri and the Firebase storage downloadlink and save it into Firebase database.
- Check if new files are on Firebase Storage that are not on the device:
  - If not:
    - Download the file.
    - Add the file to de Sqlite database locally.
   
Removal is also handled. When the user longcliks in either Bin or Favorites, the item not only get removed from the favorites
locally, but also from the cloud:
- Removing image from Firebase Storage
- Removing reference to the image from Firebase Database

Classes:
- FirebaseImage: easy way to add an image reference to the Firebase database.
- FirebaseHelper: contains all functions around syncing with the cloud.


#### Picasso

Picasso has an important part in the quick loading of all the pictures. It made it easier to handle
then using the native image loaders.


#### SwipeStack library

The SwipeStack library is made by another user, and it helped creating the core swiping element. It
was not easily possible to give the adapters and listeners from this library its own class. And thus these
classes can be found inside another class.

Classes:
- SwipeFragment: contains all function around swiping.

#### Firebase

Firebase is an important library in this application and it is used to make backups possible. Users can create
an account and start uploading their pictures. Three Firebase features are used.

Authorisation:
This is needed to support the database and storage feature.

Storage:
Firebase storage creates for every user a bucket where he or she can deposit images. It has the following
structure:
User UID:
- image1
- image2
- image..

Database:
The database is used to store favorites and downloadlinks to them. It has the following structure:

User UID:
- images:
  - UID
    - downloadUrl:
    - Uri

Classes:
- FirebaseImage: easy way to add an image reference to the Firebase database.
- FirebaseHelper: contains all functions around syncing with the cloud.
- LoginActivity: acitivity that handles login and registration.
- LoginFragment: fragment that handles login.
- RegisterFragment: fragment that handles registration

#### SQlite

Sqlite is used to keep track of the images that have been reviewed before, and manage the bin and the favorites locally.
It uses the following structure to save storage:

Bin:
Unique ID
pictures_id

Favorites:
Unique ID
pictures_id

Pictures:
Unique ID
name
album
path

Classes:
- SqliteDatabase: the class that contains most of the functions that directly affect the database.
- SqliteDatabaseHelper: helper functions that have to be executed before direct Sqlitefunctions can be executed.
- SqliteDatabaseSingleton: used to get an instance from the database from every possible place.

## Challenges

There where quite some challenges. To name a few:

#### Syncing with the cloud
I had the idea that the storage feature of Firebase worked kinda the same like the database. But that seemed not the case, I 
couldn't query Firebase storage and ask to list all the favorites for me. It took me back to the drawing board and I thought
why not combine the use of both Storage and Database. So I could query the database, and use the retrieved links to download
from the storage.

#### Gallery not updating
This was quite a tough one. I downloaded the images, and the debugger told me that all the images where on the device. But when
I opened the gallery, the images where nowhere to be found. After some research I found that files are managed trough a
'ContentResolver', that keeps track of all the files on the device. Someway I had to tell this resolver that it has to update
itself and scan the files again. There was no easy .scanFiles(); or something like that. Apperently you have to remove and 
add files to your device trough this 'ContentResolver' database, to make sure other apps 'll show the newly download files aswell.

#### Picture Skipping (SwipeStack library)
This is one of those things, you can come across when using third party libraries that aren't updated in a while. The problem
that occurs is that when you quickly swipe to the right or to the left, some images are gettings skipped. I looked trough
my own code but couldn't find a solution to the problem. I thought, maybe there are some other persons who also used this 
library and came across the same problem. So I started searching for forks, and more updated versions from the library and
kept trying different versions. The research ended in nothing, and there are still problems that can only be fixed by updating
the library code. I wanted to fix it on my own, but the lack of time made it impossible.

#### Helper classes
The classes where becoming bigger and bigger and the use of helper classes became inevitable. It took some time
to understand how this worked, and how I could take advantage of this. In the beginning I had a hard time finding 
out why I couldn't run 'getActivity()' or something similar. But when I found out that you can assign the context 
to a class variable, it was no longer a problem anymore.

## Decisions




## More time



&copy; Naam, Jaar


