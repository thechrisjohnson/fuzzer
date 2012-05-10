fuzzer
======

Fuzzer for the Security Engineering class @ RIT

Features:

Customizable to a given product within 20 minutes of configuration by your own selves in a development environment. Thus, you do not need a GUI, menu system, or anything fancy. This is a testing tool you are building for yourself!

Input discovery. Given a page, the fuzzer should attempt to discover every possible input into the system.

Parse URLs. The fuzzer should be able to take a URL and parse it down to manipulate its input and recognize which page a URL goes to. For example, http://localhost/index.jsp?something=a is the same page as http://localhost/index.jsp?anotherthing=b, and there are two input that can be fuzzed (something and anotherthing). Use Java's URL class to help with this.

Form parameters. Any time the page has a form and parameters

Time gaps. The fuzzer should allow a minimum period of time to wait between requests, where zero is a possibility

Lack of sanitization. Given inputs with data that should be sanitized, the fuzzer should report whether or not the data was actually sanitized.

Sensitive data list. As a tester, you might have some test data that you know is sensitive and should never be leaked. The system should contain a list of sensitive data, and should check each request if that data has been disclosed.

Password authentication. Given a working password, the system should be able to handle authentication into the application.

List of fuzz vectors. These are strings of common exploits to vulnerabilities. These lists can be found all over the internet. Start with this list at OWASP

Meaningful output.
	Potential vulnerabilities. The fuzzer should only report strange activity (such as system errors, http errors, or anything else that could indicate a vulnerability), or other useful information to a security tester. This output must remain readable to at least a security expert.
	Attack surface. Provide a list of all the known inputs to the system that the fuzzer discovered.

Additionally, your tool must support the following options to be turned on or off.
	Page Discovery (on|off). The fuzzer should keep a list of URLs that it can reach from an initial page.
	Page Guessing (on|off). The fuzzer should use an external list of common URLs to discover potentially unlinked pages.
	Completness (random|full). In Random mode, the fuzzer will randomly choose pages, input fields, and input texts. In Full mode, the fuzzer will try each text input on each field.
	Password guesses (on|off). Given a list of common passwords, the fuzzer should check if the system allows easily-guessable passwords.