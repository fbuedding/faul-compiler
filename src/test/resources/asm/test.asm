main:
# initializing s0 from address 0
move $s0, $0
li $v0, 8
li $v1, 4
div $t0, $v0, $v1
li $v0, 3
mul $t0, $t0, $v0
move $s0, $t0
# initializing s1 from address 4
move $s1, $0
mul $t0, $s0, $s0
move $s1, $t0
