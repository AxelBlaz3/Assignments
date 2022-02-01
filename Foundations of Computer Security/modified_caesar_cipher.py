alphabets = 'abcdefghijklmnopqrstuvwxyz'

file = open('test.txt', 'w')

cipher_text = input('Cipher: ')

for key in range(26):
    text = ''
    for x in range(len(cipher_text)):
        curr_key = (key + x)
        curr_idx = alphabets.index(cipher_text[x]) - curr_key
        if curr_idx < 0:
            curr_idx = 26 + curr_idx

        text = f'{text}{alphabets[curr_idx % 25]}'

    file.write(f'{text}, key: {key}\n')