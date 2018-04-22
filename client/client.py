import click
import cmd
import sys

from click import BaseCommand, UsageError


class REPL(cmd.Cmd):
    def __init__(self, ctx):
        cmd.Cmd.__init__(self)
        self.ctx = ctx

    def default(self, line):
        subcommand = line.split()[0]
        args = line.split()[1:]

        subcommand = cli.commands.get(subcommand)
        if subcommand:
            try:
                subcommand.parse_args(self.ctx, args)
                self.ctx.forward(subcommand)
            except UsageError as e:
                print(e.format_message())
        else:
            return cmd.Cmd.default(self, line)


@click.group(invoke_without_command=True)
@click.pass_context
def cli(ctx):
    if ctx.invoked_subcommand is None:
        repl = REPL(ctx)
        repl.cmdloop()


@cli.command()
@click.option('--foo', required=True)
def a(foo):
    print("a")
    print(foo)
    # print(c)
    return 'banana'


@cli.command()
@click.option('--foo', required=True)
def b(foo):
    print("b")
    print(foo)
    c = "asdasdasd"

if __name__ == "__main__":
    cli()