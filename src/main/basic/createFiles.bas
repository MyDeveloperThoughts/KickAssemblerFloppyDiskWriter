// Lets create the maximum 144 files on a disk and see where they end up
// This is stored as full-154.d64

// This will create 4 block files
// 1541-II with JiffyDOS and regular dos.  It dies tying to creating file 133 by trying to use track 36, sector 15
// If you then validate the disk, and continue on it creates the correctly.  Interesting

10  b$ = ""
20 for n=1to254:b$=b$+"z":next
100 for n=1 to 144
120 f$ = "file" + str$(n)
130 print "writing ";f$;"..."
140 open 1,8,6,f$ + ",s,w"
150 print#1,b$;b$;b$;b$;b$;b$;b$;b$;b$;b$;b$;b$;
160 close 1:if st<>0 then end
170 next


// 1581

10  b$ = ""
20 for n=1to254:b$=b$+"z":next
100 for n=1 to 250
120 f$ = "file" + str$(n)
130 print "writing ";f$;"..."
140 open 1,8,6,f$ + ",s,w"
150 print#1,b$;b$;b$;b$;b$;b$;b$;b$;b$;b$;b$;b$;
160 close 1:if st<>0 then end
170 next


// Running the above on a 1571 with 9 blocks each worked perfectly, no errors
// 32 blocks free (Should be 8 blocks... but we'll see later what that's about)
// on the c64 - it died creating file 244 -> v
// 243 max entries

// One giant file to see how it allocates tracks and sectors

10  b$ = ""
20 for n=1to254:b$=b$+"z":next
120 f$ = "file" + str$(n)
130 print "writing ";f$;"..."
140 open 1,8,6,f$ + ",s,w"
150 for n=1 to 150:print#1,b$;:print n:next
160 close 1



// random notes
// for directory entries.
First directory entry is on track 18 sector 1
Next directory sector is track 18 sector 4    (inter leave = 3; so it adds 3 to each directory entry sector)

Logic to find the next sector for interleaving
take current highest sector, add 3.  if > 17  (Track 18 can have at most 18 sectors starting at 0)
   then the next sector becomes the first available starting track 18 sector 1..   usually 2 is the next.



Files are placed in track 17 sector 0... goes up
Then next track is 16... thhen 15... etc

