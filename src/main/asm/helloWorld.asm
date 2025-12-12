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
.disk zinn [filename="zinn.d64", name="", id="cz", driveType="1541"]
{
         [name="TEST            ", type="prg",  segments="CODE" ]
}
