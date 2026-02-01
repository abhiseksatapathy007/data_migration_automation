<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${reportTitle}</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
            color: #333;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
        }
        .header p {
            margin: 10px 0 0 0;
            opacity: 0.9;
            font-size: 1.1em;
        }
        .content {
            padding: 30px;
        }
        .database-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 30px;
        }
        .db-card {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
        }
        .db-card h3 {
            margin: 0 0 15px 0;
            color: #495057;
            font-size: 1.2em;
        }
        .db-card p {
            margin: 5px 0;
            color: #6c757d;
        }
        .comparison-summary {
            background: #e3f2fd;
            border: 1px solid #bbdefb;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
        }
        .comparison-summary h3 {
            margin: 0 0 15px 0;
            color: #1976d2;
        }
        .summary-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        .stat-item {
            text-align: center;
            padding: 15px;
            background: white;
            border-radius: 6px;
            border: 1px solid #e0e0e0;
        }
        .stat-number {
            font-size: 2em;
            font-weight: bold;
            color: #1976d2;
        }
        .stat-label {
            color: #666;
            font-size: 0.9em;
            margin-top: 5px;
        }
        .query-results {
            margin-top: 30px;
        }
        .query-item {
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            margin-bottom: 20px;
            overflow: hidden;
        }
        .query-header {
            background: #f8f9fa;
            padding: 15px 20px;
            border-bottom: 1px solid #e0e0e0;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .query-header:hover {
            background: #e9ecef;
        }
        .query-title {
            font-weight: 600;
            color: #495057;
        }
        .customer-name {
            color: #6c757d;
            font-size: 0.9em;
            font-weight: 400;
        }
        .query-status {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.8em;
            font-weight: 600;
        }
        .status-match {
            background: #d4edda;
            color: #155724;
        }
        .status-diff {
            background: #f8d7da;
            color: #721c24;
        }
        .status-error {
            background: #fff3cd;
            color: #856404;
        }
        .query-content {
            padding: 20px;
            display: none;
        }
        .query-content.expanded {
            display: block;
        }
        .query-sql {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 6px;
            padding: 15px;
            margin-bottom: 15px;
            font-family: 'Courier New', monospace;
            font-size: 0.9em;
            color: #495057;
            white-space: pre-wrap;
        }
        .row-counts {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 15px;
        }
        .count-card {
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 6px;
            padding: 15px;
            text-align: center;
        }
        .count-number {
            font-size: 1.5em;
            font-weight: bold;
            color: #495057;
        }
        .count-label {
            color: #6c757d;
            font-size: 0.9em;
            margin-top: 5px;
        }
        .differences {
            margin-top: 15px;
        }
        .differences h4 {
            margin: 0 0 10px 0;
            color: #dc3545;
        }
        .difference-item {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            border-radius: 4px;
            padding: 10px;
            margin-bottom: 8px;
            color: #721c24;
        }
        .error-message {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 4px;
            padding: 15px;
            color: #856404;
        }
        .table-name {
            background: #e3f2fd;
            border: 1px solid #bbdefb;
            border-radius: 4px;
            padding: 8px 12px;
            display: inline-block;
            font-weight: 600;
            color: #1976d2;
            margin-bottom: 10px;
        }
        .footer {
            background: #f8f9fa;
            border-top: 1px solid #e9ecef;
            padding: 20px;
            text-align: center;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>${reportTitle}</h1>
            <p>Generated on ${generatedAt}</p>
        </div>
        
        <div class="content">
            <div class="database-info">
                <div class="db-card">
                    <h3>Source Database (NA)</h3>
                    <p>${sourceDatabase}</p>
                </div>
                <div class="db-card">
                    <h3>Target Database (EU)</h3>
                    <p>${targetDatabase}</p>
                </div>
            </div>
            
            <div class="comparison-summary">
                <h3>Comparison Summary</h3>
                <div class="summary-stats">
                    <div class="stat-item">
                        <div class="stat-number">${totalQueries}</div>
                        <div class="stat-label">Total Queries</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number" style="color: #28a745;">${passedQueries}</div>
                        <div class="stat-label">Passed</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number" style="color: #dc3545;">${failedQueries}</div>
                        <div class="stat-label">Failed</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number" style="color: #ffc107;">${errorQueries}</div>
                        <div class="stat-label">Errors</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">${passRate}</div>
                        <div class="stat-label">Pass Rate</div>
                    </div>
                </div>
            </div>
            
            <div class="query-results">
                <h3>Query Comparison Results</h3>
                
                <#list queryResults as result>
                <div class="query-item">
                    <div class="query-header" onclick="toggleQuery(${result.queryNumber})">
                        <div class="query-title">
                            Query ${result.queryNumber}: ${result.tableName}
                            <#if result.customer?has_content>
                                <span class="customer-name">(${result.customer})</span>
                            </#if>
                        </div>
                        <div class="query-status ${(result.error?? && result.error?has_content)?then('status-error', (result.hasDifferences!false)?then('status-diff', 'status-match'))}">
                            <#if result.error?? && result.error?has_content>
                                Error
                            <#elseif result.hasDifferences!false>
                                Differences Found
                            <#else>
                                Match
                            </#if>
                        </div>
                    </div>
                    
                    <div class="query-content" id="query-${result.queryNumber}">
                        <div class="table-name">Table: ${result.tableName}</div>
                        
                        <div class="query-sql">${result.query}</div>
                        
                        <div class="row-counts">
                            <div class="count-card">
                                <div class="count-number">${result.sourceRowCount}</div>
                                <div class="count-label">Source Rows</div>
                            </div>
                            <div class="count-card">
                                <div class="count-number">${result.targetRowCount}</div>
                                <div class="count-label">Target Rows</div>
                            </div>
                        </div>
                        
                        <#if result.error?? && result.error?has_content>
                            <div class="error-message">
                                <strong>Error:</strong> ${result.error}
                            </div>
                        <#elseif result.hasDifferences!false>
                            <div class="differences">
                                <h4>Differences Found:</h4>
                                <#if result.differences??>
                                    <#list result.differences as diff>
                                        <div class="difference-item">${diff}</div>
                                    </#list>
                                </#if>
                            </div>
                        <#else>
                            <div class="differences">
                                <h4 style="color: #28a745;">✓ No differences found</h4>
                            </div>
                        </#if>
                    </div>
                </div>
                </#list>
            </div>
        </div>
        
        <div class="footer">
            <p>EU Data Migration Automation - Database Comparison Report</p>
        </div>
    </div>
    
    <script>
        function toggleQuery(queryNumber) {
            const content = document.getElementById('query-' + queryNumber);
            content.classList.toggle('expanded');
        }
        
        // Auto-expand queries with differences or errors
        document.addEventListener('DOMContentLoaded', function() {
            const queryHeaders = document.querySelectorAll('.query-header');
            queryHeaders.forEach(header => {
                const status = header.querySelector('.query-status');
                if (status.classList.contains('status-diff') || status.classList.contains('status-error')) {
                    const queryNumber = header.getAttribute('onclick').match(/\d+/)[0];
                    document.getElementById('query-' + queryNumber).classList.add('expanded');
                }
            });
        });
    </script>
</body>
</html> 