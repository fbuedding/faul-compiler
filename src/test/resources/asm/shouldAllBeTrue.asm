main:
# initializing s0 with address 16
		move $s0, $0
# Loading value 6
		li $t0, 6
# Loading value 6
		li $t1, 6
# Less than equals
		sle $t0, $t0, $t1
# Assgining reg s0 with address 4
		move $s0, $t0
# initializing s1 with address 20
		move $s1, $0
		nor $t0, $s0, $s0
		andi $t0, $t0, 1
		nor $t0, $t0, $t0
		andi $t0, $t0, 1
# Assgining reg s1 with address 5
		move $s1, $t0
# initializing s2 with address 24
		move $s2, $0
# Not equals
		sne $t0, $s0, $s1
		nor $t0, $t0, $t0
		andi $t0, $t0, 1
# Assgining reg s2 with address 6
		move $s2, $t0
# unloading all vars
# unloading s0 and storing in address 16
		sw $s0, 16($gp)
# unloading s1 and storing in address 20
		sw $s1, 20($gp)
# unloading s2 and storing in address 24
		sw $s2, 24($gp)
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
