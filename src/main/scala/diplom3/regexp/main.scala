//#!/usr/local/bin/python3
//
//import argparse
//import util
//
//if __name__ == "__main__":
//    argparser = argparse.ArgumentParser()
//    argparser.add_argument("pattern",
//                           help="""Has to use . for explicit concatenation
//                                   (ie. a.b instead of ab). Doesn't support
//                                   ranges or escaping characters (so no
//                                   matching against ?, * etc)""")
//    argparser.add_argument("string", help="The string to match against")
//    args = argparser.parse_args()
//    print(util.match(args.pattern, args.string))
