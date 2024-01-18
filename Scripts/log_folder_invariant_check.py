import os
import re
import sys

invariants = r"(T1(.*?)T3(.*?)((T5(.*?)T7(.*?)T9((T11(.*?)T13(.*?)T15(.*?)T16)|(T12(.*?)T14(.*?)T15(.*?)T16)))|(T6(.*?)T8(.*?)T10(.*?)((T11(.*?)T13(.*?)T15(.*?)T16)|(T12(.*?)T14(.*?)T15(.*?)T16)))))|(T2(.*?)T4((T5(.*?)T7(.*?)T9((T11(.*?)T13(.*?)T15(.*?)T16)|(T12(.*?)T14(.*?)T15(.*?)T16)))|(T6(.*?)T8(.*?)T10((T11(.*?)T13(.*?)T15(.*?)T16)|(T12(.*?)T14(.*?)T15(.*?)T16)))))"
keep = r'\g<2>\g<3>\g<6>\g<7>\g<10>\g<11>\g<12>\g<14>\g<15>\g<16>\g<18>\g<19>\g<20>\g<23>\g<24>\g<25>\g<27>\g<28>\g<29>\g<31>\g<34>\g<35>\g<38>\g<39>\g<40>\g<42>\g<43>\g<44>\g<46>\g<47>\g<50>\g<51>\g<52>\g<54>\g<55>\g<56>'

def replace_invariants(folder_path):
    files_processed = 0
    total_respected_invariants = 0

    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)

        if os.path.isfile(file_path):
            files_processed += 1
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
                result, replacements_count = re.subn(invariants, keep, content)

                print(f"\nArchivo: {filename}")
                print(f"Invariantes encontradas: {replacements_count}")
                total_respected_invariants +=replacements_count
                
                if result.strip():  
                    print(f"***ERROR. Hubo disparos de transiciones que no respetan las invariantes***")
                    print(f"    Resto: {result}\n")
                else:
                    print(f"Todas las transiciones siguieron las invariantes con éxito.\n")


    print(f"\n	Archivos procesados: {files_processed}")
    print(f"	Total de Invariantes: {total_respected_invariants}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: log_folder_invariant_check.py <path_logs_folder>")
        sys.exit(1)

    logs_folder_path = sys.argv[1]
    replace_invariants(logs_folder_path)
