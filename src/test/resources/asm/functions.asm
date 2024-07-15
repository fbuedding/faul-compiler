main:
# initializing s0 with address 16
		move $s0, $0
		jal readI
		nop
# Assgining reg s0 with address 4
		move $s0, $v0
# initializing s1 with address 20
		move $s1, $0
		jal readI
		nop
# Assgining reg s1 with address 5
		move $s1, $v0
		mul $t0, $s0, $s1
		move $a0, $t0
		jal print
		nop
		jal exit
		nop
# unloading all vars
# unloading s0 and storing in address 16
		sw $s0, 16($gp)
# unloading s1 and storing in address 20
		sw $s1, 20($gp)
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
