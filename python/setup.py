try:
    from setuptools import setup, find_packages
except ImportError:
    from distutils.core import setup, find_packages

config = {
    'description': 'OSIAM NG Connector -- enables easy access to OSIAM NG backend',
    'author': 'Philipp Eder',
    'url': 'http://github.com/osiam-dev/osiam',
    'author_email': 'p.eder@tarent.de',
    'version': '0.1',
    'install_requires': ['nose', 'requests', 'flask', 'mock'],
#    'packages': ['osiam'],
    'packages':find_packages(exclude=['performance']),
    'scripts': [],
    'name': 'OSIAM NG Example Client'
}

setup(**config)
