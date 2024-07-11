main:
# initializing s0 with address 16
move $s0, $0
addi $s0, $0, 1
# initializing s1 with address 20
move $s1, $0
addi $s1, $0, 2
# initializing s2 with address 24
move $s2, $0
addi $s2, $0, 3
# initializing s3 with address 28
move $s3, $0
addi $s3, $0, 4
# initializing s4 with address 32
move $s4, $0
addi $s4, $0, 5
# initializing s5 with address 36
move $s5, $0
addi $s5, $0, 6
# initializing s6 with address 40
move $s6, $0
addi $s6, $0, 7
# initializing s7 with address 44
move $s7, $0
addi $s7, $0, 8
# unloading s0 and storing in address 16
sw $s0, 16($gp)
# initializing s0 with address 48
move $s0, $0
addi $s0, $0, 9
# unloading s1 and storing in address 20
sw $s1, 20($gp)
# initializing s1 with address 52
move $s1, $0
addi $s1, $0, 10
# unloading s2 and storing in address 24
sw $s2, 24($gp)
# initializing s2 with address 56
move $s2, $0
addi $s2, $0, 11
# unloading s3 and storing in address 28
sw $s3, 28($gp)
# initializing s3 with address 60
move $s3, $0
addi $s3, $0, 12
# If start
# unloading s4 and storing in address 32
sw $s4, 32($gp)
lw $s4, 16($gp)
# Loading value 1
li $t0, 1
# Equals
seq $t0, $s4, $t0
beqz $t0, label_0
nop
# unloading s5 and storing in address 36
sw $s5, 36($gp)
# initializing s5 with address 64
move $s5, $0
addi $s5, $0, 13
# unloading s6 and storing in address 40
sw $s6, 40($gp)
# initializing s6 with address 68
move $s6, $0
addi $s6, $0, 14
# unloading s7 and storing in address 44
sw $s7, 44($gp)
# initializing s7 with address 72
move $s7, $0
addi $s7, $0, 15
# If end
label_0:
# unloading s0 and storing in address 48
sw $s0, 48($gp)
# initializing s0 with address 64
move $s0, $0
# unloading s1 and storing in address 52
sw $s1, 52($gp)
lw $s1, 44($gp)
# unloading s2 and storing in address 56
sw $s2, 56($gp)
lw $s2, 48($gp)
add $t1, $s1, $s2
# unloading s3 and storing in address 60
sw $s3, 60($gp)
lw $s3, 40($gp)
add $t1, $s3, $t1
# unloading s4 and storing in address 16
sw $s4, 16($gp)
lw $s4, 36($gp)
add $t1, $s4, $t1
# unloading s6 and storing in address 68
sw $s6, 68($gp)
lw $s6, 32($gp)
add $t1, $s6, $t1
# unloading s7 and storing in address 72
sw $s7, 72($gp)
lw $s7, 28($gp)
add $t1, $s7, $t1
# unloading s0 and storing in address 64
sw $s0, 64($gp)
lw $s0, 24($gp)
add $t1, $s0, $t1
# unloading s1 and storing in address 44
sw $s1, 44($gp)
lw $s1, 20($gp)
add $t1, $s1, $t1
# unloading s2 and storing in address 48
sw $s2, 48($gp)
lw $s2, 16($gp)
add $t1, $s2, $t1
# Assgining reg null with address 16
# unloading s3 and storing in address 40
sw $s3, 40($gp)
lw $s3, 64($gp)
move $s3, $t1
# unloading all vars
# unloading s4 and storing in address 36
sw $s4, 36($gp)
# unloading s6 and storing in address 32
sw $s6, 32($gp)
# unloading s7 and storing in address 28
sw $s7, 28($gp)
# unloading s0 and storing in address 24
sw $s0, 24($gp)
# unloading s1 and storing in address 20
sw $s1, 20($gp)
# unloading s2 and storing in address 16
sw $s2, 16($gp)
# unloading s3 and storing in address 64
sw $s3, 64($gp)
# Exit
li $v0, 10
syscall
readI:
addi $sp, $sp, -4
sw $ra, 0($sp)
li $v0, 5
syscall
nop
lw $ra, 0($sp)
addi $sp, $sp, 4
jr $ra
nop
readB:
addi $sp, $sp, -4
sw $ra, 0($sp)
li $v0, 5
syscall
nop
andi $v0, $v0, 1
lw $ra, 0($sp)
addi $sp, $sp, 4
jr $ra
nop
print:
addi $sp, $sp, -4
sw $ra, 0($sp)
li $v0, 1
syscall
nop
lw $ra, 0($sp)
addi $sp, $sp, 4
jr $ra
nop
exit:
# Exit
li $v0, 10
syscall
