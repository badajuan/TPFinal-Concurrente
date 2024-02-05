import numpy as np
import getopt
import sys
import re


regex = '(T0)(.*?)((T1)(.*?)(T3)(.*?)|(T2)(.*?)(T4)(.*?))((T5)(.*?)(T7)(.*?)(T9)(.*?)|(T6)(.*?)(T8)(.*?)(TA)(.*?))((TB)(.*?)(TD)(.*?)|(TC)(.*?)(TE)(.*?))(TF)(.*?)(TG)'
groups = '\g<2>\g<5>\g<7>\g<9>\g<11>\g<14>\g<16>\g<18>\g<20>\g<22>\g<24>\g<27>\g<29>\g<31>\g<33>\g<35>'


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
    

def replace_transitions(content: str) -> str:
    '''
    Reemplaza las transiciones mayores o iguales a T10 por transiciones con
    letras, empezando por TA.

    Parameters
    ----------
    content: str
        String con transiciones.
    
    Returns
    -------
    content: str
        String modificado.
    '''

    for i in np.arange(10,17):
        content = content.replace('T'+str(i), 'T'+chr(i+55))
    
    return content


def parse_invariants(line: str, verbose: bool) -> tuple[str, int]:
    '''
    Parsea el contenido del log, buscando la cantidad de invariantes de transición cumplidos.

    Parameters
    ----------
    line: str
        String con transiciones a parsear. Es el contenido del archivo log.
    
    verbose: bool
        Si es True imprime el excedente de transiciones en cada iteración y la cuenta de invariantes encontrados.
    
    Returns
    -------
    result, total: tuple[str, int]
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
            print('Transiciones restantes:')
            print(result)
            print('Cuenta de invariantes: ' + str(total) + '\n')
    return result, total


def show_results(result: str, count: int):
    '''
    Muestra el resultado de buscar los invariantes de transición y la cuenta de
    invariantes encontrados.
    Si hay transiciones que no pertenecen a invariantes reporta FAILED.
    Si no sobran transiciones reporta OK.

    Parameters
    ----------
    result: str
        String con transiciones que no pertenecen a invariantes.

    count: int
        Cuenta de invariantes encontrados.
    '''

    print('Invariantes de transición encontrados: ' + str(count))
    if len(result) != 0:
        print('Se encuentran transiciones que no pertenecen a ningún invariante.')
        print('Transiciones: ' + result)
        print('STATUS: FAILED')
    else:
        print('STATUS: OK')


def main(argv):
    file_path = None
    verbose = False

    try:
        opts, args = getopt.getopt(argv, "hf:v", ["file=", "verbose"])
    except getopt.GetoptError:
        print("Usage: script.py -f <file_path> [-v|--verbose]")
        sys.exit(1)

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
        sys.exit(1)

    content = read_text_file(file_path)
    
    if content is not None:
        content = replace_transitions(content)
        result, total = parse_invariants(content, verbose)
        show_results(result, total)
    else:
        print('Archivo invalido')
        sys.exit(1)


if __name__ == "__main__":
    main(sys.argv[1:])