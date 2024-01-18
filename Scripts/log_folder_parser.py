import os
import re
from collections import Counter
import sys

def extract_number(transition):
    return int(re.search(r'\d+', transition).group())

def parse_logs(folder_path):
    transitions_counter = Counter()
    files_read = 0
    total_transitions = 0

    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)

        if os.path.isfile(file_path):
            files_read += 1
            with open(file_path, 'r', encoding='utf-8') as file:
                lines = file.readlines()
                transitions = re.findall(r"La transicion '(\w+)' ha sido disparada", ''.join(lines))
                transitions_counter.update(transitions)
                total_transitions += len(transitions)

    print("Resultados del Analisis:")
    print(f"    Cantidad de Logs Procesados: {files_read}")
    print(f"    Cantidad de Invariantes Procesados: {total_transitions}")
    print("Transiciones disparadas y sus conteos:")

    for transition, count in sorted(transitions_counter.items(), key=lambda x: extract_number(x[0])):
        print(f"    {transition}: {count} veces")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: log_folder_parser.py <path_folder_logs>")
        sys.exit(1)

    logs_folder_path = sys.argv[1]
    parse_logs(logs_folder_path)
