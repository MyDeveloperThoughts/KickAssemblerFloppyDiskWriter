.segment CODE []

BasicUpstart2(go)
.memblock "Main Program"
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


.disk           [filename="builtin.d64", name="STUFF", id="CZ"]
{
        [name="HELLO WORLD", type="prg", segments = "CODE"],
        [name="SID PLAYER", type="prg", prgFiles="testfiles/sidplayer.64"],
        [name="SID.OBJ.64", type="prg", prgFiles="testfiles/sid.obj.64"],
        [name="SID BURNER.MUS", type="prg", prgFiles="testfiles/sidburner.mus"]
}


.disk multidisk [filename="test.d64", name="STUFF", id="CZ", showInfo=false, driveType="1541"]
{
        [name="HELLO WORLD", type="prg<", segments = "CODE"],
        [name="SID PLAYER", type="prg", prgFiles="testfiles/sidplayer.64"],
        [name="SID.OBJ.64", type="prg", prgFiles="testfiles/sid.obj.64"],
        [name="SID BURNER.MUS", type="prg", prgFiles="testfiles/sidburner.mus"]
}

.disk multidisk [filename="test.d71", name="STUFF", id="CZ", showInfo=false,driveType="1571"]
{
        [name="SID PLAYER 128", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="SID.OBJ.128", type="prg", prgFiles="testfiles/tsonata6-3.mus"],
        [name="SID BURNER.MUS", type="prg", prgFiles="testfiles/sidburner.mus"],
        [name="1", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="2", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="3", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="4", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="5", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="6", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="7", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="8", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="9", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="10", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="11", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="12", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="13", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="14", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="15", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="16", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="17", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="18", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="19", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="20", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="21", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="22", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="23", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="24", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="25", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="26", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="27", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="28", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="29", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="30", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="31", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="32", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="33", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="34", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="35", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="36", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="37", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="38", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="39", type="prg", prgFiles="testfiles/sidplayer.128"],
        [name="30", type="prg", prgFiles="testfiles/sidplayer.128"]
}

.disk multidisk [filename="test.d81", name="STUFF", id="CZ", showInfo=true, driveType="1581"]
{
        [name="HELLO WORLD", type="prg", segments = "CODE"],
        [name="SID PLAYER", type="prg", prgFiles="testfiles/sidplayer.64"],
        [name="SID.OBJ.64", type="prg", prgFiles="testfiles/sid.obj.64"],
        [name="SID BURNER.MUS", type="prg", prgFiles="testfiles/sidburner.mus"],
         [name="1", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="2", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="3", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="4", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="5", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="6", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="7", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="8", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="9", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="10", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="11", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="12", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="13", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="14", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="15", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="16", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="17", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="18", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="19", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="20", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="21", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="22", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="23", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="24", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="25", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="26", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="27", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="28", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="29", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="30", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="31", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="32", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="33", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="34", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="35", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="36", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="37", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="38", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="39", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="40", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="41", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="42", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="43", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="44", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="45", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="46", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="47", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="48", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="49", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="50", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="51", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="52", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="53", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="54", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="55", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="56", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="57", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="58", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="59", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="60", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="61", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="62", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="63", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="64", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="65", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="66", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="67", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="68", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="69", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="70", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="71", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="72", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="73", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="74", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="75", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="76", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="77", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="78", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="79", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="80", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="81", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="82", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="83", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="84", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="85", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="86", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="87", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="88", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="89", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="90", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="91", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="92", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="93", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="94", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="95", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="96", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="97", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="98", type="prg", prgFiles="testfiles/sidplayer.64"],
         [name="99", type="prg", prgFiles="testfiles/sidplayer.64"],
}

