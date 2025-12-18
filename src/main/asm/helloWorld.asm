.segmentdef CODE
.segment CODE
BasicUpstart2(go)
     * = * "Main Program"
go:
     lda #$02
     ldx #$00
     ldy #$01
     sta $d020
     stx $d021
     sty $0286
     rts

// Create some empty disks.  These should match byte to byte with the vice-15xx.dxx disks.
// There is a batch file in the output directory that will confirm this.
.disk multidisk [filename="test-1581.d81", name="STUFF", id="CZ", driveType="1581"]
{

}
.disk multidisk [filename="test-1571.d71", name="STUFF", id="CZ", driveType="1571"]
{

}

.disk multidisk [filename="test-1541.d64", name="STUFF", id="CZ", driveType="1541"]
{

}

.disk multidisk [filename="test.d64", name="STUFF", id="CZ", driveType="1541"]
{
        [name="HELLO WORLD", type="prg", segments = "CODE"],
        [name="SID PLAYER", type="prg", prgFiles="testfiles/sidplayer.64"],
        [name="SID.OBJ.64", type="prg", prgFiles="testfiles/sid.obj.64"],
        [name="SID BURNER.MUS", type="prg", prgFiles="testfiles/sidburner.mus"],
}

.disk multidisk [filename="test.d71", name="STUFF", id="CZ", driveType="1571"]
{
        [name="HELLO WORLD", type="prg", segments = "CODE"],
        [name="SID PLAYER", type="prg", prgFiles="testfiles/sidplayer.64"],
        [name="SID.OBJ.64", type="prg", prgFiles="testfiles/sid.obj.64"],
        [name="SID BURNER.MUS", type="prg", prgFiles="testfiles/sidburner.mus"],
}

.disk multidisk [filename="test.d81", name="STUFF", id="CZ", driveType="1581"]
{
        [name="HELLO WORLD", type="prg", segments = "CODE"],
        [name="SID PLAYER", type="prg", prgFiles="testfiles/sidplayer.64"],
        [name="SID.OBJ.64", type="prg", prgFiles="testfiles/sid.obj.64"],
        [name="SID BURNER.MUS", type="prg", prgFiles="testfiles/sidburner.mus"],
}

