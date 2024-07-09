main:
# initializing s0 with address 0
move $s0, $0
addi $s0, $0, 1
# initializing s1 with address 4
move $s1, $0
addi $s1, $0, 2
# initializing s2 with address 8
move $s2, $0
addi $s2, $0, 3
# initializing s3 with address 12
move $s3, $0
addi $s3, $0, 4
# initializing s4 with address 16
move $s4, $0
addi $s4, $0, 5
# initializing s5 with address 20
move $s5, $0
addi $s5, $0, 6
# initializing s6 with address 24
move $s6, $0
addi $s6, $0, 7
# initializing s7 with address 28
move $s7, $0
addi $s7, $0, 8
# unloading s0 and storing in address 0
sw $s0, 0($gp)
# initializing s0 with address 32
move $s0, $0
addi $s0, $0, 9
# unloading s1 and storing in address 4
sw $s1, 4($gp)
# initializing s1 with address 36
move $s1, $0
addi $s1, $0, 10
# unloading s2 and storing in address 8
sw $s2, 8($gp)
# initializing s2 with address 40
move $s2, $0
addi $s2, $0, 11
# unloading s3 and storing in address 12
sw $s3, 12($gp)
# initializing s3 with address 44
move $s3, $0
addi $s3, $0, 12
# If start
# unloading s4 and storing in address 16
sw $s4, 16($gp)
lw $s4, 0($gp)
# Loading value 1
li $v0, 1
# Equals
seq $t0, $s4, $v0
beqz $t0, label_0
nop
# unloading s5 and storing in address 20
sw $s5, 20($gp)
# initializing s5 with address 48
move $s5, $0
addi $s5, $0, 13
# unloading s6 and storing in address 24
sw $s6, 24($gp)
# initializing s6 with address 52
move $s6, $0
# unloading s7 and storing in address 28
sw $s7, 28($gp)
lw $s7, -4($gp)
addi $s7, $0, 14
# unloading s0 and storing in address 32
sw $s0, 32($gp)
# initializing s0 with address 56
move $s0, $0
addi $s7, $0, 15
# If end
label_0:
# unloading s1 and storing in address 36
sw $s1, 36($gp)
# initializing s1 with address 48
move $s1, $0
# unloading s2 and storing in address 40
sw $s2, 40($gp)
lw $s2, 28($gp)
# unloading s3 and storing in address 44
sw $s3, 44($gp)
lw $s3, 32($gp)
add $t1, $s2, $s3
# unloading s4 and storing in address 0
sw $s4, 0($gp)
lw $s4, 24($gp)
add $t1, $s4, $t1
# unloading s6 and storing in address 52
sw $s6, 52($gp)
lw $s6, 20($gp)
add $t1, $s6, $t1
# unloading s0 and storing in address 56
sw $s0, 56($gp)
lw $s0, 16($gp)
add $t1, $s0, $t1
# unloading s7 and storing in address -4
sw $s7, -4($gp)
lw $s7, 12($gp)
add $t1, $s7, $t1
# unloading s1 and storing in address 48
sw $s1, 48($gp)
lw $s1, 8($gp)
add $t1, $s1, $t1
# unloading s2 and storing in address 28
sw $s2, 28($gp)
lw $s2, 4($gp)
add $t1, $s2, $t1
# unloading s3 and storing in address 32
sw $s3, 32($gp)
lw $s3, 0($gp)
add $t1, $s3, $t1
# unloading s4 and storing in address 24
sw $s4, 24($gp)
move $s4, $t1
# unloading all vars
# unloading s6 and storing in address 20
sw $s6, 20($gp)
# unloading s0 and storing in address 16
sw $s0, 16($gp)
# unloading s7 and storing in address 12
sw $s7, 12($gp)
# unloading s1 and storing in address 8
sw $s1, 8($gp)
# unloading s2 and storing in address 4
sw $s2, 4($gp)
# unloading s3 and storing in address 0
sw $s3, 0($gp)
# unloading s4 and storing in address 48
sw $s4, 48($gp)
# Exit
li $v0, 10
syscall
