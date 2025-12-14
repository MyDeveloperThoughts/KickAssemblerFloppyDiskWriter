// Lets create the maximum 144 files on a disk and see where they end up
// This is stored as full-154.d64

10 for n=1 to 144
20 f$ = "file" + str$(n)
30 print "writing ";f$;"..."
40 open 1,8,6,f$ + ",s,w"
50 print#1,"hello world"
60 close 1
70 next


// for directory entries.
First directory entry is on track 18 sector 1
Next directory sector is track 18 sector 4    (inter leave = 3; so it adds 3 to each directory entry sector)

Logic to find the next sector for interleaving
take current highest sector, add 3.  if > 17  (Track 18 can have at most 18 sectors starting at 0)
   then the next sector becomes the first available starting track 18 sector 1..   usually 2 is the next.



Files are placed in track 17 sector 0... goes up
Then next track is 16... thhen 15... etc

