function toggleCollapse(id) {
    unhighlight();
    const content = document.getElementById(id);
    const collapsible = content.previousElementSibling;

    if (content.style.display === 'block' || content.style.display === '') {
        content.style.display = 'none';
        collapsible.classList.remove('expanded');
    } else {
        content.style.display = 'block';
        collapsible.classList.add('expanded');
    }
    console.log('Toggled: ' + id);
}

function highlightSearch(text) {
    unhighlight(); // Unhighlight previous results
    window.findHighlightCalled = false; // Reset the scroll flag

    if (!text) return;

    var xpath = "//text()[contains(., '" + text + "')]";
    //var xpath = "//text()[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + text.toLowerCase() + "')]";
    var matches = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);

    for (var i = 0; i < matches.snapshotLength; i++) {
        var textNode = matches.snapshotItem(i);
        expandParentCollapsible(textNode);
        highlightTextNode(textNode, text);
    }
}

function expandParentCollapsible(node) {
    var count = 0;
    while (node != null && node.tagName !== 'BODY') {
        count++;
        console.log(count);
        if (node.classList && node.classList.contains('collapsible')) {
            // Expand the collapsible content
            var content = node.nextElementSibling;
            if (content && content.style.display !== 'block') {
                content.style.display = 'block';
                node.classList.add('expanded');
            }
        } else if (node.classList && node.classList.contains('emptyBox')) {
            // Expand the group collapse
            var groupContent = node;
            if (groupContent && groupContent.style.display !== 'block') {
                groupContent.style.display = 'block';
                //node.classList.add('expanded');
            }
        }
        node = node.parentNode; // Continue traversing up the DOM
    }
}

function highlightTextNode(node, text) {
    // Create a regex pattern that treats underscores and brackets as part of the word
    var boundary = "(?:\\b|(?<!\\w)[_\\[\\]]?)";
    var pattern = new RegExp(boundary + escapeRegExp(text) + boundary, "gi");
    var content = node.nodeValue;
    var match, matches = [];

    // Use exec to find all matches in the text node
    while ((match = pattern.exec(content)) !== null) {
        // Avoid infinite loops with zero-width matches
        if (match.index === pattern.lastIndex) {
            pattern.lastIndex++;
        }
        var span = document.createElement('span');
        span.className = 'highlighted';
        var start = match.index;
        var end = start + match[0].length;
        var middleBit = node.splitText(start);
        var endBit = middleBit.splitText(end - start);
        var middleClone = middleBit.cloneNode(true);
        span.appendChild(middleClone);
        middleBit.parentNode.replaceChild(span, middleBit);

        matches.push(span); // Add the span to the matches array

        // Reset node to the end bit for next iteration
        node = endBit;
    }

    // Scroll the first highlighted text into view
    if (matches.length > 0 && !window.findHighlightCalled) {
        matches[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
        window.findHighlightCalled = true;
    }
}


function escapeRegExp(text) {
    return text.replace(/[-[\]/{}()*+?.\\^$|]/g, "\\$&");
}

function unhighlight() {
    const highlighted = document.querySelectorAll('.highlighted');
    highlighted.forEach(function(node) {
        const parent = node.parentNode;
        parent.replaceChild(document.createTextNode(node.textContent), node);
        parent.normalize();
    });
}