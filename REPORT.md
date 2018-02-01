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

#### Loading images


#### SwipeStack library

#### Firebase 

#### SQlite
