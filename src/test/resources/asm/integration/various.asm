main:
# initializing s0 with address 16
move $s0, $0
# Loading value 100
li $t0, 100
# Loading value 12
li $t1, 12
div $t0, $t0, $t1
# Assgining reg s0 with address 4
move $s0, $t0
move $a0, $s0
jal print
nop
# unloading all vars
# unloading s0 and storing in address 16
sw $s0, 16($gp)
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
