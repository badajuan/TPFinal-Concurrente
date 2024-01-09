import getopt
import sys


def parse_props(props_line):
    props_list = props_line.split()[1:]
    props_dict = {}

    for prop in props_list:
        if '*' in prop:
            prop_name, prop_count = prop.split('*')
            props_dict[prop_name] = int(prop_count)
        else:
            props_dict[prop] = 1

    return props_dict


def parse_config(config_file_path):
    with open(config_file_path, 'r') as config_file:
        return config_file.read().split()


def main(argv):
    input_file_path = None
    output_file_path = None
    config_file_path = None

    try:
        opts, args = getopt.getopt(argv, "hi:o:c:", ["input=", "output=", "config="])
    except getopt.GetoptError:
        print("parser.py -i <input_file> -o <output_file> -c <config_file>")
        sys.exit(2)

    for opt, arg in opts:
        if opt == "-h":
            print("parser.py -i <input_file> -o <output_file> -c <config_file>")
            sys.exit()
        elif opt in ("-i", "--input"):
            input_file_path = arg
        elif opt in ("-o", "--output"):
            output_file_path = arg
        elif opt in ("-c", "--config"):
            config_file_path = arg

    if not input_file_path or not output_file_path or not config_file_path:
        print("Input, output, and config file paths are required.")
        sys.exit(2)

    # Properties list (P0, P1, ..., P19)
    properties_list = [f"P{i}" for i in range(20)]

    # Read configuration file
    config_places = parse_config(config_file_path)

    with open(input_file_path, 'r') as input_file:
        states_props = {}

        for line in input_file:
            if line.startswith("state"):
                state_number = line.split()[1]
                states_props[state_number] = {}

            elif line.startswith("props"):
                props_dict = parse_props(line)
                states_props[state_number] = props_dict

    # Calculate the max value of the "SUM" column
    max_sum_value = max(sum(props_dict.get(place, 0) for place in config_places) for state, props_dict in states_props.items())

    with open(output_file_path, 'w') as output_file:
        # Write max value at the beginning
        output_file.write(f"Max SUM: {max_sum_value}\n")

        # Write header
        output_file.write("S " + " ".join(properties_list + ["SUM"]) + "\n")

        # Write data
        for state, props_dict in states_props.items():
            output_file.write(state)
            sum_value = sum(props_dict.get(place, 0) for place in config_places)
            for prop in properties_list:
                output_file.write(f" {props_dict.get(prop, 0)}")
            output_file.write(f" {sum_value}\n")

    print(f"Parsing complete. Output written to {output_file_path}")


if __name__ == "__main__":
    main(sys.argv[1:])