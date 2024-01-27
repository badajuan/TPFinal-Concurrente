import os
import re
import sys

invariants = r'(T1)(.*?)((T3)(.*?)(((T5)(.*?)(T7)(.*?)(T9)(.*?)(((T11)(.*?)(T13)(.*?)(T15)(.*?)(T16))|((T12)(.*?)(T14)(.*?)(T15)(.*?)(T16))))|((T6)(.*?)(T8)(.*?)(T10)(.*?)(((T11)(.*?)(T13)(.*?)(T15)(.*?)(T16))|((T12)(.*?)(T14)(.*?)(T15)(.*?)(T16))))))|((T2)(.*?)(T4)(.*?)(((T5)(.*?)(T7)(.*?)(T9)(.*?)(((T11)(.*?)(T13)(.*?)(T15)(.*?)(T16))|((T12)(.*?)(T14)(.*?)(T15)(.*?)(T16))))|((T6)(.*?)(T8)(.*?)(T10)(.*?)(((T11)(.*?)(T13)(.*?)(T15)(.*?)(T16))|((T12)(.*?)(T14)(.*?)(T15)(.*?)(T16))))))'
keep = r'\g<2>\g<5>\g<9>\g<11>\g<13>\g<17>\g<19>\g<21>\g<25>\g<27>\g<29>\g<33>\g<35>\g<37>\g<41>\g<43>\g<45>\g<49>\g<51>\g<53>\g<57>\g<59>\g<63>\g<65>\g<67>\g<71>\g<73>\g<75>\g<79>\g<81>\g<83>\g<87>\g<89>\g<91>\g<95>\g<97>\g<99>\g<103>\g<105>\g<107>'

def replace_invariants(folder_path):
    files_processed = 0
    total_respected_invariants = 0

    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)
        if os.path.isfile(file_path):
            files_processed += 1
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
                content = re.sub(r'T0', '', content)
                replacements_count = 0
                #for i in range(0, 4):
                #while True:
                    result, replacements = re.subn(invariants, keep, content)
                    replacements_count += replacements
                    if (result == content)|(len(result)==0) : break
                    else: content = result
                    print(len(result))
                    

                print(f"\nArchivo: {filename}")
                print(f"Invariantes reemplazadas: {replacements_count}")
                
                if result.strip():  #Hay resto
                    print(f"***ERROR. Hubo disparos de transiciones que no respetan las invariantes***")
                    print(f"    Resto: {result}\n")
                else:
                    print(f"Todas las transiciones siguieron las invariantes con éxito.\n")


    print(f"\n	Archivos procesados: {files_processed}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: log_folder_invariant_check.py <path_logs_folder>")
        sys.exit(1)

    logs_folder_path = sys.argv[1]
    replace_invariants(logs_folder_path)
