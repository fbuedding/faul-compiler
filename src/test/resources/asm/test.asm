main:
sw $0, 0($gp)
li $v0, 8
li $v1, 4
div $t0, $v0, $v1
li $v0, 3
mul $t0, $t0, $v0
sw $t0, 0($gp)
sw $0, 4($gp)
lw $s0, 0($gp)
lw $s1, 0($gp)
mul $t0, $s0, $s1
sw $t0, 4($gp)
