from typing import List

def decorator (func):
    print(func.__doc__)

@decorator(param=1)
def f(x):
    """
    Syntax Highlighting Demo
    @param x Parameter

    Semantic highlighting:
    Generated spectrum to pick colors for local variables and parameters:
     Color#1 SC1.1 SC1.2 SC1.3 SC1.4 Color#2 SC2.1 SC2.2 SC2.3 SC2.4 Color#3
     Color#3 SC3.1 SC3.2 SC3.3 SC3.4 Color#4 SC4.1 SC4.2 SC4.3 SC4.4 Color#5
    """

    def nested_func(y, j="hello"):
        print(y + 1)

    nested_func(y=1, j="world")

    s = ("Test", 2+3, {'a': 'b'}, f'{x!s:{"^10"}}')   # Comment
    f(s[0].lower())
    nested_func(42)

class MyType:
    def __init__(self, T):
        self.sense = T

class Foo:
    tags: List[List[z[str,str]]]
    def __init__(self, tags):
        self.tags = tags
        MyType
    def __init__(self, T):
        self.tags = T

    def __init__(self: List[foo]):
        byte_string: bytes = b'newline:\n also newline:\x0a'
        text_string = u"Cyrillic Я is \u042f. Oops: \u042g"
        self.make_sense(whatever=1)

    def make_sense[T](self, whatever: T):
        self.sense = whatever

x = len('abc')
type my_int< = int
print(f.__doc__)
def func1[T, **T](whatever T)

def make_sense[T](self, whatever: T):
    self.sense = whatever