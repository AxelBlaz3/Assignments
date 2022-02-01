import sys
import secrets

# Max number of bits to generate by custom PRNG.
N_BITS = 500000

# Track number of zeros generated so far. Initially none, so 0.
zeros = 0

if __name__ == '__main__':
    
    # Loop over N_BITS and generate either a 0 or 1.
    for i in range(N_BITS):
        random_bit = secrets.choice([0, 1])
        if random_bit == 0:
            zeros += 1

    # Print tracked 0s and 1s.
    print(f'Zeros: {zeros}, Ones: {N_BITS - zeros}')
