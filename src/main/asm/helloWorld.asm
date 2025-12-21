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




.disk multidisk [filename="my1581image.d64", name="STUFF", id="CZ"]
{
        [name="HELLO WORLD", type="prg", segments = "CODE"]
}
