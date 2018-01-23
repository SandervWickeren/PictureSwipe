# Process Book

Diary of progression trough the weeks.

## Week 1

### Monday 15/01/18
- Moved imageloading to its own class
- Added cardview to application
- Cardview shows filename and picture
- Added supported formats: .png, .jpg, .jpeg
- BUG 1: Cards are randomly getting skipped --> Possible library bug
  
### Tuesday 16/01/18
 - Designed SQLite tables
 - Add and delete functions for SQlite tables written
 - Singleton class necessary for the database added
 - Added buttons bin, fav and next
 - Searched for forked projects from the SwipeStack library. Hoping
 to find a possible bugfix for bug 1. Couldn't find a solution so
 I have to fix it by myself --> Quite a challenge, postponed to week 3.
 
 ### Wednesday 17/01/18
 - Visual overlay when swiping: green --> next picture, red --> move to bin.
 it changes its alpha value based on the swipedistance.
    - Possible feature: thumbs and cross in overlay.
    - Bug 2: Overlay not visible when using the buttons.
 - Tried to change overlay to the specific card instead of the complete view
 but failed
 - Updated the CardView layout:
    - Better borders
    - Now showing name instead of path
 - Functions added to the SwipeFragment to add rated pictures to the pcitures
 or bin database.
 - Temporary list under the FavoritesFragment containing all names from the 
 picture table --> Used to debug the db atm.
 
 ### Thursday 18/01/18
 - Because of the heavy storm and travelling all day, no progression :(.
 
 ### Friday 19/01/18
 - Fixed errors when adding pictures to bin / pictures database.
 - Binfragment now shows list of files inside the bin
 - Swipefragment no longer resets on regaining focus.
 - Added temporary clear db buttons in account fragment.
 
 ### Monday 22/01/18
- Researching the use of GridView.
- Adding 'path' into database.
  - Took an extra amount of time because the application was reusing backups from
  previous databases of the same app. Because this backup database has a different
  structure, errors kept coming. Fixed it eventually by overwriting the allowance
  for backups ('ll re-enable it at release).
- Succesfully created GridView containing images from the Bin Database using Picasso.
- Now asking for permissions when it isn't granted yet (TODO: What if they decline?)

### Tuesday 23/01/18
- Favorites now also shows the correct pictures
- Removal of unused class because of the switch from ListView to GridView
- Changed BottomNavigation from (account, swipe, fav, bin) --> (bin, swipe, fav, account)
- New transition when swiping images:
  - Transparancy is now exponentially calculated instead of linear.
  - The overlay is no longer a big square --> now meaningfull icons.
- When clicking on the image at either favorites or bin the pictures 'll be shown bigger:
  - Currently dialog fragment --> not sure if I keep it this way.
- Current TODO's / Problems:
  - Onclicklistener needs its own class --> Can't call .getactivity()
  - Duplicate code / Overall code cleanup
  - Ability to remove items from favorites / bin.
  - Login and backup favorites.
 

