import getopt
import sys
import re

#'\g<2>\g<4>\g<6>\g<8>\g<10>\g<12>\g<14>\g<16>\g<18>'
#'\g<4>\g<6>\g<9>\g<11>\g<15>\g<17>\g<19>\g<22>\g<24>\g<26>\g<30>\g<32>\g<35>\g<37>\g<39>\g<41>'

regex = '(T0)(((.*?)(T1)(.*?)(T3))|((.*?)(T2)(.*?)(T4)))(((.*?)(T5)(.*?)(T7)(.*?)(T9))|((.*?)(T6)(.*?)(T8)(.*?)(T10)))(((.*?)(T11)(.*?)(T13))|((.*?)(T12)(.*?)(T14)))(.*?)(T15)(.*?)(T16)'

def read_text_file(file_path):
    try:
        with open(file_path, 'r') as file:
            content = file.read()
            return content
    except FileNotFoundError:
        print(f"File '{file_path}' not found.")
    except Exception as e:
        print(f"An error occurred: {e}")
        return None

def parse_invariants(line: str):
    total = 0
    while (True):
        result, found = re.subn(regex, '\g<4>\g<6>\g<9>\g<11>\g<15>\g<17>\g<19>\g<22>\g<24>\g<26>\g<30>\g<32>\g<35>\g<37>\g<39>\g<41>', line, count=0)
        print(result + ' ' + str(found))
        if found == 0:
            break
        total = total + found
        line = result
    print(total)
        

def main(argv):
    file_path = None

    try:
        opts, args = getopt.getopt(argv, "hf:", ["file="])
    except getopt.GetoptError:
        print("Usage: script.py -f <file_path>")
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print("Usage: script.py -f <file_path>")
            sys.exit()
        elif opt in ("-f", "--file"):
            file_path = arg

    if file_path is None:
        print("File path is required. Use -f or --file.")
        sys.exit(2)

    content = read_text_file(file_path)

    if content is not None:
        parse_invariants(content)

if __name__ == "__main__":
    main(sys.argv[1:])