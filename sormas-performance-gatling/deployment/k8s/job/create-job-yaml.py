#!/usr/bin/env python3

import argparse
import uuid

from jinja2 import Template


def write_job_file(template, output, name, java_opts, simulation_name):
    template = Template(open(template, 'rt').read())
    content = template.render(name=name, java_opts=java_opts, simulation_name=simulation_name)
    open(output, 'wt').write(content)
    print(f"Wrote {output}")


def parse_args():
    parser = argparse.ArgumentParser(description='Create Kubernetes Job YAML file')
    id = f"{str(uuid.uuid4())[:8]}"
    parser.add_argument('--out',
                        default=f"job.yaml",
                        help='Job filename')
    parser.add_argument('--name',
                        default=f"gatling-java-example-{id}",
                        help='Job name')
    parser.add_argument('--java_opts',
                        default='-DbaseUrl=http://localhost:8080 -DrequestPerSecond=10 -DdurationMin=0.25',
                        help='Java opts')
    parser.add_argument('--simulation',
                        default="ExampleSimulation",
                        help='Simulation name')
    return parser.parse_args()


args = parse_args()
write_job_file('job-template.yaml', args.out, args.name, args.java_opts, args.simulation)
