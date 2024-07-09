main:
# initializing s0 with address 0
move $s0, $0
# Loading value 8
li $v0, 8
# Loading value 4
li $v1, 4
div $t0, $v0, $v1
# Loading value 3
li $v0, 3
mul $t0, $t0, $v0
move $s0, $t0
# initializing s1 with address 4
move $s1, $0
# negating $s
sub $t0, $0, $s0
mul $t0, $s0, $t0
move $s1, $t0
# initializing s2 with address 8
move $s2, $0
# Loading value 5
li $v0, 5
# Modulo
div $s1, $v0
mfhi $t0
move $s2, $t0
# initializing s3 with address 12
move $s3, $0
# Loading value 1
li $v0, 1
nor $t0, $v0, $v0
andi $t0, $t0, 1
move $s3, $t0
# unloading all vars
# unloading s0 and storing in address 0
sw $s0, 0($gp)
# unloading s1 and storing in address 4
sw $s1, 4($gp)
# unloading s2 and storing in address 8
sw $s2, 8($gp)
# unloading s3 and storing in address 12
sw $s3, 12($gp)
# Exit
li $v0, 10
syscall
