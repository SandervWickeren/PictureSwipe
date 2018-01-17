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

