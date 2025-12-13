.segmentdef CODE
.segment CODE
BasicUpstart2(go)
     * = * "Main Program"
go:
     lda #$02
     sta $d020
     rts



// .disk [filename="test.d64", name="PLUGIN DISK", id="12", showInfo=true]

// .disk Consolez [name="plugin disk"]
// .disk [filename="test.d64", name="", id="", showInfo=true]
//.disk zinn [filename="test-1541.d64", name="STUFF", id="CZ", driveType="1541"]
//{
//        [name="TEST            ", type="prg",  segments="CODE" ]
//}

.disk zinn [filename="test-1571.d64", name="STUFF", id="CZ", driveType="1571"]
{
        [name="TEST            ", type="prg",  segments="CODE" ]
}
