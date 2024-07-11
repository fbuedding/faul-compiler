main:
# initializing s0 with address 16
move $s0, $0
addi $s0, $0, 0
# If start
# Loading value 2
li $t0, 2
# Loading value 2
li $t1, 2
# Modulo
div $t0, $t1
mfhi $t0
# Loading value 0
li $t1, 0
# Equals
seq $t0, $t0, $t1
beqz $t0, label_0
nop
addi $s0, $0, 2
# If end
label_0:
# If start
# Loading value 1
li $t1, 1
beqz $t1, label_2
nop
# initializing s1 with address 20
move $s1, $0
addi $s1, $0, 1
b label_1
nop
# else start
label_2:
# initializing s2 with address 20
move $s2, $0
addi $s2, $0, 2
# If end
label_1:
# initializing s3 with address 20
move $s3, $0
addi $s3, $0, 3
# unloading all vars
# unloading s0 and storing in address 16
sw $s0, 16($gp)
# unloading s3 and storing in address 20
sw $s3, 20($gp)
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
