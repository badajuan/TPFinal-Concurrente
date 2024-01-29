import getopt
import sys
import re

regex = '(T0)(.*?)((T1)(.*?)(T3)(.*?)|(T2)(.*?)(T4)(.*?))((T5)(.*?)(T7)(.*?)(T9)(.*?)|(T6)(.*?)(T8)(.*?)(TA)(.*?))((TB)(.*?)(TD)(.*?)(TF)(.*?)(TG)|(TC)(.*?)(TE)(.*?)(TF)(.*?)(TG))'
groups = '\g<2>\g<5>\g<7>\g<9>\g<11>\g<14>\g<16>\g<18>\g<20>\g<22>\g<24>\g<27>\g<29>\g<31>\g<34>\g<36>\g<38>'


def read_text_file(file_path: str):
    '''
    Abre el archivo de log en modo solo lectura.

    Parameters
    ----------
    file_path: str
        Nombre del archivo.
    '''
    
    try:
        with open(file_path, 'r') as file:
            content = file.read()
            return content
    except FileNotFoundError:
        print(f"File '{file_path}' not found.")
    except Exception as e:
        print(f"An error occurred: {e}")
        return None


def parse_invariants(line: str, verbose: bool) -> (str, int):
    '''
    Parsea el contenido del log, buscando la cantidad de invariantes de transición cumplidos.
    Al finalizar imprime el resto no perteneciente a ningún invariante y la cantidad de
    invariantes encontrados.

    Parameters
    ----------
    line: str
        String con transiciones a parsear. Es el contenido del archivo log.
    
    verbose: bool
        Si es True imprime el excedente de transiciones en cada iteración y la cuenta de invariantes encontrados.
    
    Return
    ------
    result, total: tuple(str, int)
        String de transiciones que no pertenecen a un invariante y total de invariantes encontrados.
    '''
    
    total = 0
    while (True):
        result, found = re.subn(regex, groups, line, count=0)
        if found == 0:
            break
        total = total + found
        line = result
        if verbose is True:
            print('Resto:')
            print(result)
            print('Cuenta de invariantes: ' + str(total) + '\n')
    return result, total
    

def main(argv):
    file_path = None
    verbose = False

    try:
        opts, args = getopt.getopt(argv, "hf:v", ["file=", "verbose"])
    except getopt.GetoptError:
        print("Usage: script.py -f <file_path> [-v|--verbose]")
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print("Usage: script.py -f <file_path>")
            sys.exit()
        elif opt in ("-f", "--file"):
            file_path = arg
        elif opt in ("-v", "--verbose"):
            verbose = True

    if file_path is None:
        print("File path is required. Use -f or --file.")
        sys.exit(2)

    content = read_text_file(file_path)
    
    content = content.replace("T10","TA")
    content = content.replace("T11","TB")
    content = content.replace("T12","TC")
    content = content.replace("T13","TD")
    content = content.replace("T14","TE")
    content = content.replace("T15","TF")
    content = content.replace("T16","TG")

    if content is not None:
        result, total = parse_invariants(content, verbose)
        print('Invariantes de transición encontrados: ' + str(total))
        if len(result) != 0:
            print('Se encuentran transiciones que no pertenecen a ningún invariante.')
            print('Transiciones: ' + result)
            print('STATUS: FAILED')
        else:
            print('STATUS: OK')

if __name__ == "__main__":
    main(sys.argv[1:])