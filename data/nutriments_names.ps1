$csv = Import-Csv .\nutriments_names.csv -Delimiter ',' -Encoding "utf8" -Header A,B

$query = "insert into nutriments_names (id, name) values "
$stringBuilder = New-Object System.Text.StringBuilder
$null = $stringBuilder.Append($query)


$csv | foreach {
    $A = $_.A
    $B = $_.B.replace("'", "\'")
    $null = $stringBuilder.Append("(`'$A`', `'$B`'),`r`n")
}

$query = $stringBuilder.ToString()
$query = $query.Substring(0, $query.Length - 3)

$query > .\nutriments_names.sql