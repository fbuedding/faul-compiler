main:
# initializing s0 with address 0
move $s0, $0
# Loading value 6
li $v0, 6
# Loading value 6
li $v1, 6
# Less than equals
sle $t0, $v0, $v1
move $s0, $t0
# initializing s1 with address 4
move $s1, $0
nor $t0, $s0, $s0
andi $t0, $t0, 1
nor $t0, $t0, $t0
andi $t0, $t0, 1
move $s1, $t0
# initializing s2 with address 8
move $s2, $0
# Not equals
sne $t0, $s0, $s1
nor $t0, $t0, $t0
andi $t0, $t0, 1
move $s2, $t0
# unloading all vars
# unloading s0 and storing in address 0
sw $s0, 0($gp)
# unloading s1 and storing in address 4
sw $s1, 4($gp)
# unloading s2 and storing in address 8
sw $s2, 8($gp)
# Exit
li $v0, 10
syscall
