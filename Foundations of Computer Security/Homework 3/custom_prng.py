import time

# Initial occurences of zeros and ones.
occ_zeros = 0
occ_ones = 0

# Max number of bits to generate by custom PRNG.
N_BITS = 500000

""" Custom hash function for generating a bit (0 or 1)."""


def custom_hash(index):
    return round((time.time() * 1000) % N_BITS + index) & 1


# Loop until generated 1s and 0s are equal to N_BITS
i = 0
while occ_ones + occ_zeros < N_BITS:
    first_rand_bit = custom_hash(i)
    second_rand_bit = custom_hash(i + 3)

    if first_rand_bit != second_rand_bit:
        if first_rand_bit == 0:
            occ_zeros += 1
        else:
            occ_ones += 1

        i += 1

# Print tracked 0s and 1s along with their probabilities.
print(f'Zeros: {occ_zeros}, Ones: {N_BITS - occ_zeros}\np0: {occ_zeros / N_BITS:.1f}, p1: {occ_ones / N_BITS:.1f}')
